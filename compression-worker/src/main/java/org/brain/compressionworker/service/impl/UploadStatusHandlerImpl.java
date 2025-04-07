package org.brain.compressionworker.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.compressionworker.model.ObjectStatus;
import org.brain.compressionworker.model.TokenStatus;
import org.brain.compressionworker.model.UploadToken;
import org.brain.compressionworker.service.ObjectMetadataService;
import org.brain.compressionworker.service.UploadStatusHandler;
import org.brain.compressionworker.service.UploadTokenService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UploadStatusHandlerImpl implements UploadStatusHandler {

    private final ObjectMetadataService objectMetadataService;
    private final UploadTokenService uploadTokenService;

    @Override
    @Transactional
    public void handleUploadStatus(UploadToken uploadToken, String errorMessage, ObjectStatus objectStatus, TokenStatus tokenStatus) {
        log.error("Update status for token {}: status: {}, error: {}", uploadToken.getUploadToken(), objectStatus, errorMessage);
        uploadTokenService.updateUploadTokenStatus(uploadToken, tokenStatus, errorMessage);
        objectMetadataService.updateStatus(uploadToken.getObjectId(), objectStatus, errorMessage);

    }

    @Override
    public void handleUploadTokenStatus(UploadToken uploadToken, TokenStatus tokenStatus, String errorMessage) {
        log.error("Upload token status update for token {}: status: {}", uploadToken.getUploadToken(), errorMessage);
        uploadTokenService.updateUploadTokenStatus(uploadToken, tokenStatus, errorMessage);
    }

}
