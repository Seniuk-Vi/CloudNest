package org.brain.compressionworker.listener;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.compressionworker.exception.S3UploadFailed;
import org.brain.compressionworker.model.ObjectStatus;
import org.brain.compressionworker.model.TokenStatus;
import org.brain.compressionworker.model.UploadToken;
import org.brain.compressionworker.repository.redis.RedisRepository;
import org.brain.compressionworker.service.CompressionService;
import org.brain.compressionworker.service.ObjectMetadataService;
import org.brain.compressionworker.service.S3Service;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@AllArgsConstructor
public class KafkaCompressionListener {

    private final CompressionService compressionService;
    private final S3Service s3Service;
    private final RedisRepository redisRepository;
    private final ObjectMetadataService objectMetadataService;
    private final int MAX_RETRIES = 3;
    private final int BACKOFF_INTERVAL = 1000;

    @KafkaListener(topics = "${spring.kafka.topic.file-compression}")
    @Transactional
    public void fileCompressionListener(UploadToken message) {

        log.info("Received message: {}", message);

        // download object from S3
        byte[] objectData = s3Service.downloadObject(message.getFilePath());

        // compress object
        byte[] compressedData = compressObject(objectData, message);

        // upload compressed data to main bucket
        try {
            uploadToMainBucketWithBackOff(compressedData, message.getFilePath());
        } catch (S3UploadFailed e) {
            log.error("Failed to upload compressed data to main bucket for file: {}", message.getFilePath(), e);
            message.setStatus(TokenStatus.FAILED);
            message.setError(e.getMessage());
            redisRepository.save(message);
            objectMetadataService.updateStatus(message.getObjectId(), ObjectStatus.FAILED, e.getMessage());
            return;
        }

        // update status to UPLOADED
        // todo: 2PC commit
        message.setStatus(TokenStatus.UPLOADED);
        redisRepository.save(message);
        objectMetadataService.updateStatus(message.getObjectId(), ObjectStatus.UPLOADED, null);
    }

    private byte[] compressObject(byte[] objectData, UploadToken uploadToken) {
        log.info("Compressing object with upload token: {}", uploadToken.getUploadToken());
        uploadToken.setStatus(TokenStatus.COMPRESSING);
        redisRepository.save(uploadToken);
        byte[] compressedData = compressionService.compress(objectData);
        return compressedData;
    }

    private void uploadToMainBucketWithBackOff(byte[] compressedData, String filePath) throws S3UploadFailed {
        log.info("Uploading compressed data to main bucket for file: {}", filePath);
        int retries = 0;
        while (retries < MAX_RETRIES) {
            try {
                s3Service.uploadToMainBucket(compressedData, filePath);
                return;
            } catch (Exception e) {
                log.error("Failed to upload compressed data to main bucket for file: {}", filePath, e);
                retries++;
                try {
                    Thread.sleep(BACKOFF_INTERVAL);
                } catch (InterruptedException ex) {
                    log.error("Thread interrupted while backing off", ex);
                    throw new S3UploadFailed(ex);
                }
            }
        }
    }

}