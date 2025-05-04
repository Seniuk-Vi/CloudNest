package org.brain.compressionworker.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.brain.compressionworker.exception.S3DownloadFailed;
import org.brain.compressionworker.exception.S3UploadFailed;
import org.brain.compressionworker.service.S3Service;
import org.brain.compressionworker.service.TempFileService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.AsyncRequestBodyFromInputStreamConfiguration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.concurrent.*;

@Service
@Slf4j
public class S3ServiceImpl implements S3Service {

    private final String STAGING_BUCKET;

    private final String MAIN_BUCKET;

    private final ExecutorService executorService;

    private final int PART_SIZE = 100 * 1024 * 1024;

    private final S3TransferManager s3TransferManager;

    private final TempFileService tempFileService;

    public S3ServiceImpl(
            @Value("${spring.cloud.aws.s3.bucket.staging}") String STAGING_BUCKET,
            @Value("${spring.cloud.aws.s3.bucket.main}") String MAIN_BUCKET,
            @Qualifier("asyncExecutor") ExecutorService asyncExecutor,
            S3TransferManager s3TransferManager,
            TempFileService tempFileService) {
        this.STAGING_BUCKET = STAGING_BUCKET;
        this.MAIN_BUCKET = MAIN_BUCKET;
        this.executorService = asyncExecutor;
        this.s3TransferManager = s3TransferManager;
        this.tempFileService = tempFileService;
    }

    @Override
    public void uploadToMainBucket(File file, String key) throws S3UploadFailed {
        try {
            // Step 1: Initialize multipart upload
            log.info("Starting upload for file to bucket: {} with key: {}", MAIN_BUCKET, key);

            // Ensure the file exists and is readable
            if (!file.exists() || !file.isFile()) {
                throw new S3UploadFailed("File does not exist or is not a valid file: " + file.getAbsolutePath());
            }

            // Create the AsyncRequestBody from the file's InputStream
            AsyncRequestBodyFromInputStreamConfiguration requestBodyConf =
                    AsyncRequestBodyFromInputStreamConfiguration.builder()
                            .contentLength(file.length())
                            .inputStream(new FileInputStream(file))
                            .executor(executorService)
                            .build();
            AsyncRequestBody requestBody = AsyncRequestBody.fromInputStream(requestBodyConf);

            // Build the UploadRequest
            UploadRequest uploadRequest = UploadRequest.builder()
                    .requestBody(requestBody)
                    .putObjectRequest(builder -> builder.bucket(MAIN_BUCKET).key(key))
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


    /**
     * Download an object from S3 and return its content as a byte array.
     *
     * @param filePath The key of the object in the S3 bucket.
     * @return The content of the object as a byte array.
     */
    public File downloadObject(String filePath) throws S3DownloadFailed {
        // create temp file for download
        try {
            File tempFile = tempFileService.createTempFile(filePath);
            DownloadFileRequest downloadFileRequest = DownloadFileRequest.builder()
                    .getObjectRequest(GetObjectRequest.builder()
                            .bucket(STAGING_BUCKET)
                            .key(filePath)
                            .build())
                    .destination(tempFile)
                    .build();
            FileDownload downloadFile = s3TransferManager.downloadFile(downloadFileRequest);
            CompletedFileDownload downloadResult = downloadFile.completionFuture().join();
            if (downloadResult.response().sdkHttpResponse().isSuccessful()) {
                log.info("File downloaded successfully from S3: {}", filePath);
            } else {
                log.error("Failed to download file from S3: {}. Error: {}", filePath, downloadResult.response().sdkHttpResponse().statusText());
            }
            return tempFile;
        } catch (Exception e) {
            log.error("Failed to download file from S3: {}. Error: {}", filePath, e.getMessage());
            throw new S3DownloadFailed(e);
        }
    }
}
