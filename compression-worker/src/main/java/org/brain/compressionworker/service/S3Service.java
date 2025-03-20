package org.brain.compressionworker.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class S3Service {

    private final S3Client s3Client;

    private final String STAGING_BUCKET;

    public S3Service(S3Client s3Client, @Value("${spring.cloud.aws.s3.bucket.staging}") String STAGING_BUCKET) {
        this.s3Client = s3Client;
        this.STAGING_BUCKET = STAGING_BUCKET;
    }

    public void uploadToStagingBucket(MultipartFile file, String key) {
        try {
            // Step 1: Initialize multipart upload
            CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                    .bucket(STAGING_BUCKET)
                    .key(key)
                    .build();
            CreateMultipartUploadResponse createMultipartUploadResponse = s3Client.createMultipartUpload(createMultipartUploadRequest);
            String uploadId = createMultipartUploadResponse.uploadId();

            // Step 2: Split the file into parts and upload each part
            List<CompletedPart> completedParts = uploadParts(file, uploadId, key);

            // Step 3: Complete the multipart upload
            CompleteMultipartUploadRequest completeMultipartUploadRequest = CompleteMultipartUploadRequest.builder()
                    .bucket(STAGING_BUCKET)
                    .key(key)
                    .uploadId(uploadId)
                    .multipartUpload(CompletedMultipartUpload.builder().parts(completedParts).build())
                    .build();
            s3Client.completeMultipartUpload(completeMultipartUploadRequest);

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    private List<CompletedPart> uploadParts(MultipartFile file, String uploadId, String key) throws IOException {
        List<CompletedPart> completedParts = new ArrayList<>();
        long partSize = 5 * 1024 * 1024; // 5 MB per part
        long fileSize = file.getSize();
        byte[] fileBytes = file.getBytes();

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
}
