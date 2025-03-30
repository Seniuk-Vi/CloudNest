package org.brain.compressionworker.service;


import org.brain.compressionworker.exception.S3UploadFailed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    private final S3Client s3Client;

    private final String STAGING_BUCKET;

    private final String MAIN_BUCKET;

    public S3Service(S3Client s3Client,
                     @Value("${spring.cloud.aws.s3.bucket.staging}") String STAGING_BUCKET,
                        @Value("${spring.cloud.aws.s3.bucket.main}") String MAIN_BUCKET) {
        this.s3Client = s3Client;
        this.STAGING_BUCKET = STAGING_BUCKET;
        this.MAIN_BUCKET = MAIN_BUCKET;
    }

    public void uploadToMainBucket(byte[] fileBytes, String key) throws S3UploadFailed {
        try {
            // Step 1: Initialize multipart upload
            CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                    .bucket(MAIN_BUCKET)
                    .key(key)
                    .build();
            CreateMultipartUploadResponse createMultipartUploadResponse = s3Client.createMultipartUpload(createMultipartUploadRequest);
            String uploadId = createMultipartUploadResponse.uploadId();

            // Step 2: Split the file into parts and upload each part
            List<CompletedPart> completedParts = uploadParts(fileBytes, uploadId, key);

            // Step 3: Complete the multipart upload
            CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(MAIN_BUCKET)
                    .key(key)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())
                    .build();
            s3Client.completeMultipartUpload(completeMultipartUploadRequest);

        } catch (Exception e) {
            throw new S3UploadFailed(e);
        }
    }

    private List<CompletedPart> uploadParts(byte[] fileBytes, String uploadId, String key) throws IOException {
        List<CompletedPart> completedParts = new ArrayList<>();
        long partSize = 5 * 1024 * 1024; // 5 MB per part
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
                    .bucket(MAIN_BUCKET)
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

    /**
     * Download an object from S3 and return its content as a byte array.
     *
     * @param objectKey  The key of the object in the S3 bucket.
     * @return The content of the object as a byte array.
     */
    public byte[] downloadObject(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(STAGING_BUCKET)
                .key(objectKey)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            byte[] buffer = new byte[8192]; // Buffer size for reading
            int bytesRead;
            while ((bytesRead = s3Object.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            return outputStream.toByteArray(); // Return the object content as a byte array
        } catch (IOException e) {
            throw new RuntimeException("Failed to download object from S3", e);
        }
    }

}
