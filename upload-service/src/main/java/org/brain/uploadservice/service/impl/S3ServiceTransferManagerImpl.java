package org.brain.uploadservice.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.brain.uploadservice.exception.S3UploadFailed;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncRequestBodyFromInputStreamConfiguration;
import software.amazon.awssdk.core.async.BlockingInputStreamAsyncRequestBody;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.FileUpload;
import software.amazon.awssdk.transfer.s3.model.Upload;
import software.amazon.awssdk.transfer.s3.model.UploadFileRequest;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;

import java.io.File;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@Service
@Slf4j
public class S3ServiceTransferManagerImpl {

    private final String STAGING_BUCKET;

    private final S3TransferManager s3TransferManager;

    private final ExecutorService asyncExecutor;

    public S3ServiceTransferManagerImpl(S3TransferManager s3TransferManager, @Value("${spring.cloud.aws.s3.bucket.staging}") String STAGING_BUCKET, @Qualifier("asyncExecutor") ExecutorService asyncExecutor) {
        this.s3TransferManager = s3TransferManager;
        this.STAGING_BUCKET = STAGING_BUCKET;
        this.asyncExecutor = asyncExecutor;
    }

    public void uploadToStagingBucket(MultipartFile file, String key) throws S3UploadFailed {
        try {
            log.info("Starting upload for file: {} to bucket: {} with key: {}", file.getName(), STAGING_BUCKET, key);

            AsyncRequestBodyFromInputStreamConfiguration requestBodyConf = AsyncRequestBodyFromInputStreamConfiguration.builder()
                    .contentLength(file.getSize())
                    .inputStream(file.getInputStream())
                    .executor(asyncExecutor)
                    .build();
            AsyncRequestBody requestBody = AsyncRequestBody.fromInputStream(requestBodyConf);
            UploadRequest uploadRequest = UploadRequest.builder()
                    .requestBody(requestBody) // Path to the file
                    .putObjectRequest(builder -> builder.bucket(STAGING_BUCKET).key(key))
                    .build();

            // Start the upload
            Upload fileUpload = s3TransferManager.upload(uploadRequest);
            fileUpload.completionFuture().join();

            log.info("File uploaded successfully to S3: {}", key);
        } catch (Exception e) {
            log.error("Failed to upload file to S3: {}. Error: {}", key, e.getMessage());
            throw new S3UploadFailed(e);
        }
    }

}
