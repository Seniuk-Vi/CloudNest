package org.brain.uploadservice.service;

import org.brain.uploadservice.model.UploadToken;

import java.util.Base64;

public class UploadTokenGenerator {

    public static UploadToken generateUploadToken(Long objectId) {
        String rawToken = objectId + ":" + System.currentTimeMillis();
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(rawToken.getBytes());
        return new UploadToken(token, objectId);
    }
}