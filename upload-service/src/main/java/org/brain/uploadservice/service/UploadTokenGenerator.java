package org.brain.uploadservice.service;

import java.util.Base64;

public class UploadTokenGenerator {

    public static String generateUploadToken(Long objectId) {
        String rawToken = objectId + ":" + System.currentTimeMillis();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawToken.getBytes());
    }
}