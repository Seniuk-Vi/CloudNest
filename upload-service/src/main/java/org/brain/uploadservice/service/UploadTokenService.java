package org.brain.uploadservice.service;


import org.brain.uploadservice.model.TokenStatus;
import org.brain.uploadservice.model.UploadToken;

public interface UploadTokenService {

    UploadToken updateUploadTokenStatus(UploadToken uploadToken, TokenStatus status, String errorMessage);


}
