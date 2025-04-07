package org.brain.uploadservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.uploadservice.model.ObjectStatus;
import org.brain.uploadservice.model.TokenStatus;
import org.brain.uploadservice.model.UploadToken;
import org.brain.uploadservice.payload.ObjectResponse;
import org.brain.uploadservice.repository.redis.RedisRepository;
import org.brain.uploadservice.service.UploadService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final ObjectMetadataService objectMetadataService;

    private final RedisRepository redisRepository;

    private final S3ServiceTransferManagerImpl s3Service;

    private final KafkaPublisher kafkaPublisher;

    private final String S3_UPLOAD_FAILED = "S3 upload failed";


    @Transactional
    @Override
    public ObjectResponse handleFileUpload(MultipartFile file, UUID parentFolderId) {
        log.info("Handling file upload for file: {}", file.getOriginalFilename());
        // create object record
        ObjectResponse objectResponse = objectMetadataService.createObject(file.getOriginalFilename(), parentFolderId, file.getSize());
        String filePath = objectMetadataService.getObjectPath(objectResponse.getObjectId());

        // generate upload token
        UploadToken uploadToken = UploadTokenGenerator.generateUploadToken(objectResponse.getObjectId());
        objectResponse.setUploadToken(uploadToken.getUploadToken());

        // save upload token to Redis
        uploadToken.setStatus(TokenStatus.STAGING);
        uploadToken.setFilePath(filePath);
        redisRepository.save(uploadToken);
        log.info("Created object record for file: {} with upload token: {}", file.getOriginalFilename(), uploadToken);

        // stage file asynchronously
        stageFile(file, uploadToken, objectResponse.getUserId().toString());

        return objectResponse;
    }


    public void stageFile(MultipartFile file, UploadToken uploadToken, String groupId) {

        try {
            log.info("Staging file with upload token: {}", uploadToken.getUploadToken());

            long startTime = System.currentTimeMillis();
            s3Service.uploadToStagingBucket(file, uploadToken.getFilePath());
            // log time in seconds
            log.info("Time taken to upload: {} to S3: {} seconds", uploadToken.getUploadToken(), (System.currentTimeMillis() - startTime) / 1000);
            log.debug("Staged file with upload token: {}", uploadToken.getUploadToken());

            uploadToken.setStatus(TokenStatus.WAITING_FOR_COMPRESSING);
            redisRepository.save(uploadToken);
            log.debug("Updated status for upload token: {} to WAITING_FOR_COMPRESSING", uploadToken);

            kafkaPublisher.publishCompressionMessage(uploadToken, groupId);
            log.info("Published compression message for upload token: {}", uploadToken);
        } catch (Exception e) {
            log.error("Failed to stage file with upload token: {}. Exception: {}", uploadToken.getUploadToken(), e.getMessage());
            uploadToken.setStatus(TokenStatus.FAILED);
            uploadToken.setError(S3_UPLOAD_FAILED);
            redisRepository.save(uploadToken);
            objectMetadataService.updateStatus(uploadToken.getObjectId(), ObjectStatus.FAILED, S3_UPLOAD_FAILED);
        }
    }

}
