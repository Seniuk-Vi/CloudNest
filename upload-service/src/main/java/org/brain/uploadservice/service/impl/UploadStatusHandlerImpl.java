package org.brain.uploadservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.uploadservice.model.ObjectStatus;
import org.brain.uploadservice.model.TokenStatus;
import org.brain.uploadservice.model.UploadToken;
import org.brain.uploadservice.service.UploadStatusHandler;
import org.brain.uploadservice.service.UploadTokenService;
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
        log.info("Upload token status update for token {}: status: {}", uploadToken.getUploadToken(), errorMessage);
        uploadTokenService.updateUploadTokenStatus(uploadToken, tokenStatus, errorMessage);
    }

}
