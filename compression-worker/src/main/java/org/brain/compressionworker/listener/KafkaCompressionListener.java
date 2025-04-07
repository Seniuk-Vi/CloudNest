package org.brain.compressionworker.listener;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.compressionworker.exception.CompressionFailed;
import org.brain.compressionworker.exception.S3DownloadFailed;
import org.brain.compressionworker.exception.S3UploadFailed;
import org.brain.compressionworker.model.ObjectStatus;
import org.brain.compressionworker.model.TokenStatus;
import org.brain.compressionworker.model.UploadToken;
import org.brain.compressionworker.repository.redis.RedisRepository;
import org.brain.compressionworker.service.CompressionService;
import org.brain.compressionworker.service.ObjectMetadataService;
import org.brain.compressionworker.service.UploadStatusHandler;
import org.brain.compressionworker.service.impl.S3ServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletionException;


@Service
@Slf4j
@AllArgsConstructor
public class KafkaCompressionListener {

    private final CompressionService compressionService;
    private final S3ServiceImpl s3Service;
    private final UploadStatusHandler uploadStatusHandler;

    @KafkaListener(topics = "${spring.kafka.topic.file-compression}")
    @Transactional
    public void fileCompressionListener(UploadToken message, Acknowledgment acknowledgment) throws S3DownloadFailed {

        log.info("Received message: {}", message);
        try {
            // download object from S3
            File objectData = s3Service.downloadObject(message.getFilePath());

            // compress object
            File compressedData = null;
            try {
                compressedData = compressObject(objectData, message);
            } catch (CompressionFailed e) {
                throw new RuntimeException(e);
            }

            // upload compressed data to main bucket
            s3Service.uploadToMainBucket(compressedData, message.getFilePath());


            // update status to UPLOADED
            uploadStatusHandler.handleUploadStatus(message, null, ObjectStatus.UPLOADED, TokenStatus.UPLOADED);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            // Log the error and do not acknowledge the message
            log.error("Error processing message: {}. Message will not be acknowledged and will be retried.", message, e);

            // Optionally, handle specific exceptions (e.g., update status to FAILED)
            uploadStatusHandler.handleUploadStatus(message, e.getMessage(), ObjectStatus.FAILED, TokenStatus.FAILED);
        }
    }

    private File compressObject(File file, UploadToken uploadToken) throws CompressionFailed {
        log.info("Compressing object with upload token: {}", uploadToken.getUploadToken());
        uploadStatusHandler.handleUploadTokenStatus(uploadToken, TokenStatus.COMPRESSING, null);
        try {
            return compressionService.compress(file, uploadToken.getFilePath());
        } catch (IOException e) {
            log.error("Failed to compress object: {}", uploadToken.getFilePath(), e);
            uploadStatusHandler.handleUploadStatus(uploadToken, e.getMessage(), ObjectStatus.FAILED, TokenStatus.FAILED);
            throw new CompressionFailed(e);
        }
    }


}