package org.brain.uploadservice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.uploadservice.model.ObjectStatus;
import org.brain.uploadservice.model.TokenStatus;
import org.brain.uploadservice.model.UploadToken;
import org.brain.uploadservice.payload.ObjectResponse;
import org.brain.uploadservice.repository.redis.RedisRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UploadService {

    private final ObjectMetadataService objectMetadataService;

    private final RedisRepository redisRepository;

    private final S3Service s3Service;

    private final KafkaPublisher kafkaPublisher;

    private final String S3_UPLOAD_FAILED = "S3 upload failed";

    private final int MAX_RETRIES = 3;

    private final int BACKOFF_INTERVAL = 1000;

    @Transactional
    public ObjectResponse handleFileUpload(MultipartFile file, UUID parentFolderId) {
        log.info("Handling file upload for file: {}", file.getOriginalFilename());
        ObjectResponse objectResponse = objectMetadataService.createObject(file.getOriginalFilename(), parentFolderId, file.getSize());

        UploadToken uploadToken = UploadTokenGenerator.generateUploadToken(objectResponse.getObjectId());
        objectResponse.setUploadToken(uploadToken.getUploadToken());

        uploadToken.setStatus(TokenStatus.STAGING);
        redisRepository.save(uploadToken);
        log.info("Created object record for file: {} with upload token: {}", file.getOriginalFilename(), uploadToken);

        stageFile(file, uploadToken);

        return objectResponse;
    }

    @Async
    @Transactional
    public void stageFile(MultipartFile file, UploadToken uploadToken) {
        try {
            log.info("Staging file: {} with upload token: {}", file.getOriginalFilename(), uploadToken);

            s3Service.uploadToStagingBucket(file, uploadToken.getUploadToken());
            log.debug("Staged file: {} with upload token: {}", file.getOriginalFilename(), uploadToken);

            // todo: persistance exception due to missing context of uploadToken
            uploadToken.setStatus(TokenStatus.WAITING_FOR_COMPRESSING);
            redisRepository.save(uploadToken);
            log.debug("Updated status for upload token: {} to WAITING_FOR_COMPRESSING", uploadToken);

            kafkaPublisher.publishCompressionMessage(uploadToken);
            log.info("Published compression message for upload token: {}", uploadToken);
        } catch (Exception e) {
            log.error("Failed to stage file: {} with upload token: {}. Exception: {}", file.getOriginalFilename(), uploadToken, e.getMessage());
            uploadToken.setStatus(TokenStatus.FAILED);
            uploadToken.setError(S3_UPLOAD_FAILED);
            redisRepository.save(uploadToken);
            objectMetadataService.updateStatus(uploadToken.getObjectId(), ObjectStatus.FAILED, S3_UPLOAD_FAILED);
        }
    }
}
