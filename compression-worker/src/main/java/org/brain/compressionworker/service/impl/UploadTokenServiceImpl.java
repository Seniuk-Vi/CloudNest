package org.brain.compressionworker.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.compressionworker.model.TokenStatus;
import org.brain.compressionworker.model.UploadToken;
import org.brain.compressionworker.repository.redis.RedisRepository;
import org.brain.compressionworker.service.UploadTokenService;
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
