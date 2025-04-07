package org.brain.compressionworker.service;

import org.brain.compressionworker.model.TokenStatus;
import org.brain.compressionworker.model.UploadToken;

public interface UploadTokenService {

    UploadToken updateUploadTokenStatus(UploadToken uploadToken, TokenStatus status, String errorMessage);


}
