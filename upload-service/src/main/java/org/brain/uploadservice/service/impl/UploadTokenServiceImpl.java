package org.brain.uploadservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.uploadservice.model.TokenStatus;
import org.brain.uploadservice.model.UploadToken;
import org.brain.uploadservice.repository.redis.RedisRepository;
import org.brain.uploadservice.service.UploadTokenService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UploadTokenServiceImpl implements UploadTokenService {

    private final RedisRepository redisRepository;

    @Override
    public UploadToken updateUploadTokenStatus(UploadToken uploadToken, TokenStatus status, String errorMessage) {
        uploadToken.setStatus(status);
        if (errorMessage != null) {
            uploadToken.setError(errorMessage);
        }
        redisRepository.save(uploadToken);
        return uploadToken;
    }




}
