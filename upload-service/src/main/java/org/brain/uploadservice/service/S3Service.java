package org.brain.uploadservice.service;


import io.opentelemetry.context.Context;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.brain.uploadservice.exception.S3UploadFailed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Service
@Slf4j
public class S3Service {

    private final S3Client s3Client;

    private final String STAGING_BUCKET;

    private final ExecutorService executorService;

    private final int PART_SIZE = 100 * 1024 * 1024; // 100 MB

    public S3Service(S3Client s3Client, @Value("${spring.cloud.aws.s3.bucket.staging}") String STAGING_BUCKET) {
        this.s3Client = s3Client;
        this.STAGING_BUCKET = STAGING_BUCKET;

        this.executorService = Context.taskWrapping(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }

    public void uploadToStagingBucket(byte[] file, String key) throws S3UploadFailed {
        // Step 1: Initialize multipart upload
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(STAGING_BUCKET)
                .key(key)
                .build();
        CreateMultipartUploadResponse createMultipartUploadResponse = s3Client.createMultipartUpload(createMultipartUploadRequest);
        String uploadId = createMultipartUploadResponse.uploadId();

        try {
            // Step 2: Split the file into parts and upload each part
            List<CompletedPart> completedParts = uploadPartsInParallel(file, uploadId, key);

            // Step 3: Complete the multipart upload
            CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(STAGING_BUCKET)
                    .key(key)
                    .uploadId(uploadId)
                    .multipartUpload((builder -> builder.parts(completedParts)))
                    .build();
            s3Client.completeMultipartUpload(completeMultipartUploadRequest);

        } catch (Exception e) {
            s3Client.abortMultipartUpload(AbortMultipartUploadRequest.builder()
                    .bucket(STAGING_BUCKET)
                    .key(key)
                    .uploadId(uploadId)
                    .build());
            throw new S3UploadFailed(e);
        }
    }

    // upload without parts
    public void uploadToStagingBucketSingleUpload(byte[] file, String key) throws S3UploadFailed {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(STAGING_BUCKET)
                    .key(key)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file));
        } catch (Exception e) {
            throw new S3UploadFailed(e);
        }
    }

    private List<CompletedPart> uploadParts(byte[] fileBytes, String uploadId, String key) throws IOException {
        List<CompletedPart> completedParts = new ArrayList<>();
        long partSize = 10 * 1024 * 1024; // 5 MB per part
        long fileSize = fileBytes.length;

        int partNumber = 1;
        for (long offset = 0; offset < fileSize; offset += partSize) {
            long remainingBytes = fileSize - offset;
            long currentPartSize = Math.min(partSize, remainingBytes);

            // Extract the current part's bytes
            byte[] partBytes = new byte[(int) currentPartSize];
            System.arraycopy(fileBytes, (int) offset, partBytes, 0, (int) currentPartSize);

            // Upload the part
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(STAGING_BUCKET)
                    .key(key)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .build();
            UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest, RequestBody.fromBytes(partBytes));

            // Collect the completed part information
            completedParts.add(CompletedPart.builder()
                    .partNumber(partNumber)
                    .eTag(uploadPartResponse.eTag())
                    .build());

            partNumber++;
        }

        return completedParts;
    }

    private List<CompletedPart> uploadPartsInParallel(byte[] fileBytes, String uploadId, String key) throws InterruptedException, ExecutionException {
        long fileSize = fileBytes.length;
        long partSize = determinePartSize(fileSize);
        List<Future<CompletedPart>> futures = submitPartUploadTasks(fileBytes, uploadId, key, partSize);
        return collectCompletedParts(futures);
    }

    private List<Future<CompletedPart>> submitPartUploadTasks(byte[] fileBytes, String uploadId, String key, long partSize) {
        List<Future<CompletedPart>> futures = new ArrayList<>();
        long fileSize = fileBytes.length;
        int totalParts = (int) Math.ceil((double) fileSize / partSize);

        for (int partNumber = 1; partNumber <= totalParts; partNumber++) {
            final int currentPartNumber = partNumber;
            final long offset = (currentPartNumber - 1) * partSize;
            final long currentPartSize = Math.min(partSize, fileSize - offset);

            byte[] partBytes = new byte[(int) currentPartSize];
            System.arraycopy(fileBytes, (int) offset, partBytes, 0, (int) currentPartSize);

            futures.add(executorService.submit(() -> uploadPart(partBytes, uploadId, key, currentPartNumber)));
        }

        return futures;
    }

    private List<CompletedPart> collectCompletedParts(List<Future<CompletedPart>> futures) throws InterruptedException, ExecutionException {
        List<CompletedPart> completedParts = new ArrayList<>();
        for (Future<CompletedPart> future : futures) {
            completedParts.add(future.get());
        }
        return completedParts;
    }

    private CompletedPart uploadPart(byte[] partBytes, String uploadId, String key, int partNumber) throws Exception {
        return retryWithBackoff(() -> {
            UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                    .bucket(STAGING_BUCKET)
                    .key(key)
                    .uploadId(uploadId)
                    .partNumber(partNumber)
                    .build();
            UploadPartResponse uploadPartResponse = s3Client.uploadPart(uploadPartRequest, RequestBody.fromBytes(partBytes));
            return CompletedPart.builder()
                    .partNumber(partNumber)
                    .eTag(uploadPartResponse.eTag())
                    .build();
        }, 3, 1000, 8000, "upload part " + partNumber);
    }

    private <T> T retryWithBackoff(Callable<T> task, int maxRetries, long initialBackoff, long maxBackoff, String taskDescription) throws Exception {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return task.call();
            } catch (Exception e) {
                log.error("Failed to execute {} (attempt {} of {}). Error: {}", taskDescription, attempt, maxRetries, e.getMessage());
                if (attempt == maxRetries) {
                    throw e;
                }
                long backoffTime = Math.min(initialBackoff * (1L << (attempt - 1)), maxBackoff);
                try {
                    log.info("Retrying {} after {} ms", taskDescription, backoffTime);
                    Thread.sleep(backoffTime);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
        throw new RuntimeException("Unexpected error in retryWithBackoff for " + taskDescription);
    }

    /**
     * Determines the optimal part size based on the file size and thread count.
     *
     * @param fileSize   The total size of the file in bytes.
     * @return The calculated part size in bytes.
     */
    private long determinePartSize(long fileSize) {
        int threadCount = Runtime.getRuntime().availableProcessors();
        // Dynamically calculate part size to ensure no more parts than threads
        long partSize = (long) Math.ceil((double) fileSize / threadCount);

        // Ensure part size is at least the minimum required by AWS S3 (5 MB)
        return Math.max(partSize, PART_SIZE); // 5 MB
    }

    @PreDestroy
    public void cleanup() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
