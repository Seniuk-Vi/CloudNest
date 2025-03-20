package org.brain.uploadservice.service;

import org.brain.uploadservice.model.UploadToken;

import java.util.Base64;
import java.util.UUID;

public class UploadTokenGenerator {

    public static UploadToken generateUploadToken(UUID objectId) {
        String rawToken = objectId + ":" + System.currentTimeMillis();
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(rawToken.getBytes());
        return new UploadToken(token, objectId);
    }
}