package org.brain.uploadservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.uploadservice.model.ObjectStatus;
import org.brain.uploadservice.model.TokenStatus;
import org.brain.uploadservice.model.UploadToken;
import org.brain.uploadservice.service.UploadStatusHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@AllArgsConstructor
public class StageFileService {

    private final S3ServiceTransferManagerImpl s3Service;

    private final KafkaPublisher kafkaPublisher;

    private final UploadStatusHandler uploadStatusHandler;

    private final String S3_UPLOAD_FAILED = "S3 upload failed";

    public void stageFile(MultipartFile file, UploadToken uploadToken, String groupId) {
        try {
            log.info("Staging file with upload token: {}", uploadToken.getUploadToken());

            long startTime = System.currentTimeMillis();
            s3Service.uploadToStagingBucket(file, uploadToken.getFilePath());
            // log time in seconds
            log.info("Time taken to upload: {} to S3: {} seconds", uploadToken.getUploadToken(), (System.currentTimeMillis() - startTime) / 1000);
            log.debug("Staged file with upload token: {}", uploadToken.getUploadToken());

            uploadToken.setStatus(TokenStatus.WAITING_FOR_COMPRESSING);
            uploadStatusHandler.handleUploadTokenStatus(uploadToken, TokenStatus.WAITING_FOR_COMPRESSING, null);
            log.debug("Updated status for upload token: {} to WAITING_FOR_COMPRESSING", uploadToken);

            kafkaPublisher.publishCompressionMessage(uploadToken, groupId);
            log.info("Published compression message for upload token: {}", uploadToken);
        } catch (Exception e) {
            log.error("Failed to stage file with upload token: {}. Exception: {}", uploadToken.getUploadToken(), e.getMessage());
            uploadStatusHandler.handleUploadStatus(uploadToken, S3_UPLOAD_FAILED, ObjectStatus.FAILED, TokenStatus.FAILED);
            throw new RuntimeException("Failed to stage file", e);
        }
    }

}
