package org.brain.uploadservice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.uploadservice.model.ObjectStatus;
import org.brain.uploadservice.model.TokenStatus;
import org.brain.uploadservice.model.UploadToken;
import org.brain.uploadservice.repository.redis.RedisRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@AllArgsConstructor
public class AsyncStageFileService {

    private final ObjectMetadataService objectMetadataService;

    private final RedisRepository redisRepository;

    private final S3Service s3Service;

    private final KafkaPublisher kafkaPublisher;

    private final String S3_UPLOAD_FAILED = "S3 upload failed";

    @Async
    @Transactional
    public void stageFile(MultipartFile file, UploadToken uploadToken, String groupId) {
        try {
            log.info("Staging file: {} with upload token: {}", file.getOriginalFilename(), uploadToken);

            long startTime = System.currentTimeMillis();
            s3Service.uploadToStagingBucket(file.getBytes(), uploadToken.getFilePath());
            // log time in seconds
            log.info("Time taken to upload file: {} to S3: {} seconds", file.getOriginalFilename(), (System.currentTimeMillis() - startTime) / 1000);
            log.debug("Staged file: {} with upload token: {}", file.getOriginalFilename(), uploadToken);

            uploadToken.setStatus(TokenStatus.WAITING_FOR_COMPRESSING);
            redisRepository.save(uploadToken);
            log.debug("Updated status for upload token: {} to WAITING_FOR_COMPRESSING", uploadToken);

            kafkaPublisher.publishCompressionMessage(uploadToken, groupId);
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
