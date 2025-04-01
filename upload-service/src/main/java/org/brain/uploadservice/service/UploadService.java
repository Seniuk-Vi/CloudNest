package org.brain.uploadservice.service;

import io.opentelemetry.context.Context;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.uploadservice.model.ObjectStatus;
import org.brain.uploadservice.model.TokenStatus;
import org.brain.uploadservice.model.UploadToken;
import org.brain.uploadservice.payload.ObjectResponse;
import org.brain.uploadservice.repository.redis.RedisRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UploadService {

    private final ObjectMetadataService objectMetadataService;

    private final RedisRepository redisRepository;

    private final AsyncStageFileService asyncStageFileService;

    @Transactional
    public ObjectResponse handleFileUpload(MultipartFile file, UUID parentFolderId) {
        log.info("Handling file upload for file: {}", file.getOriginalFilename());
        // create object record
        ObjectResponse objectResponse = objectMetadataService.createObject(file.getOriginalFilename(), parentFolderId, file.getSize());
        String filePath = objectMetadataService.getObjectPath(objectResponse.getObjectId());
        // generate upload token
        UploadToken uploadToken = UploadTokenGenerator.generateUploadToken(objectResponse.getObjectId());
        objectResponse.setUploadToken(uploadToken.getUploadToken());

        // save upload token to Redis
        uploadToken.setStatus(TokenStatus.STAGING);
        uploadToken.setFilePath(filePath);
        redisRepository.save(uploadToken);
        log.info("Created object record for file: {} with upload token: {}", file.getOriginalFilename(), uploadToken);

        // stage file asynchronously
        asyncStageFileService.stageFile(file, uploadToken, objectResponse.getUserId().toString());

        return objectResponse;
    }

}
