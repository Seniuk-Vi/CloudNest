package org.brain.uploadservice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.uploadservice.exception.S3UploadFailed;
import org.brain.uploadservice.model.ObjectStatus;
import org.brain.uploadservice.model.TokenStatus;
import org.brain.uploadservice.payload.ObjectResponse;
import org.brain.uploadservice.repository.RedisRepository;
import org.brain.uploadservice.repository.S3Repository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.function.Supplier;

@Service
@Slf4j
@AllArgsConstructor
public class UploadService {

    private final ObjectMetadataService objectMetadataService;

    private final RedisRepository redisRepository;

    private final S3Repository s3Repository;

    private final KafkaPublisher kafkaPublisher;

    private final String S3_UPLOAD_FAILED = "S3_UPLOAD_FAILED";

    private final int MAX_RETRIES = 3;

    private final int BACKOFF_INTERVAL = 1000;

    @Transactional
    public ObjectResponse handleFileUpload(MultipartFile file, Long parentFolderId) {
        log.info("Handling file upload for file: {}", file.getOriginalFilename());
        ObjectResponse objectResponse = objectMetadataService.createObject(file.getOriginalFilename(), parentFolderId, file.getSize());
        String uploadToken = UploadTokenGenerator.generateUploadToken(objectResponse.getObjectId());
        objectResponse.setUploadToken(uploadToken);
        redisRepository.createObjectRecord(uploadToken, TokenStatus.STAGING.getDescription());
        log.info("Created object record for file: {} with upload token: {}", file.getOriginalFilename(), uploadToken);

        stageFile(file, uploadToken, objectResponse.getObjectId());

        return objectResponse;
    }

    @Async
    @Transactional
    public void stageFile(MultipartFile file, String uploadToken, Long objectId) {
        try {
            log.info("Staging file: {} with upload token: {}", file.getOriginalFilename(), uploadToken);

            executeWithRetry(() -> s3Repository.uploadToStagingBucket(file, uploadToken));
            log.debug("Staged file: {} with upload token: {}", file.getOriginalFilename(), uploadToken);

            redisRepository.updateStatus(uploadToken, TokenStatus.WAITING_FOR_COMPRESSING.getDescription());
            log.debug("Updated status for upload token: {} to WAITING_FOR_COMPRESSING", uploadToken);

            kafkaPublisher.publishCompressionMessage(uploadToken);
            log.info("Published compression message for upload token: {}", uploadToken);
        } catch (Exception e) {
            log.error("Failed to stage file: {} with upload token: {}. Exception: {}", file.getOriginalFilename(), uploadToken, e.getMessage());
            redisRepository.updateStatus(uploadToken, TokenStatus.FAILED.getDescription());
            objectMetadataService.updateStatus(objectId, ObjectStatus.FAILED);
        }
    }

    public void executeWithRetry(Supplier<Void> operation) throws S3UploadFailed {
        int retries = 0;
        Exception lastException = null;
        while (retries < MAX_RETRIES) {
            try {
                operation.get();
                return;
            } catch (Exception e) {
                lastException = e;
                retries++;
                try {
                    Thread.sleep((long) Math.pow(2, retries) * BACKOFF_INTERVAL);
                } catch (InterruptedException ignored) {}
            }
        }
        throw new S3UploadFailed(lastException);
    }
}
