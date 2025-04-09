package org.brain.uploadservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.uploadservice.model.TokenStatus;
import org.brain.uploadservice.model.UploadToken;
import org.brain.uploadservice.payload.ObjectResponse;
import org.brain.uploadservice.service.UploadService;
import org.brain.uploadservice.service.UploadStatusHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.transfer.s3.model.Upload;

import java.util.UUID;

@Service
@Slf4j
@AllArgsConstructor
public class UploadServiceImpl implements UploadService {

    private final ObjectMetadataService objectMetadataService;

    private final StageFileService stageFileService;

    private final UploadStatusHandler uploadStatusHandler;

    @Override
    public ObjectResponse handleFileUpload(MultipartFile file, UUID parentFolderId) {
        log.info("Handling file upload for file: {}", file.getOriginalFilename());
        // create object
        ObjectResponse objectResponse = createObject(file, parentFolderId);
        UploadToken uploadToken = generateUploadToken(objectResponse.getObjectId());
        objectResponse.setUploadToken(uploadToken.getUploadToken());
        uploadToken.setFilePath(objectResponse.getFilePath());
        // stage file
        stageFileService.stageFile(file, uploadToken, objectResponse.getUserId().toString());

        return objectResponse;
    }


    public ObjectResponse createObject(MultipartFile file, UUID parentFolderId) {
        // Create object record
        ObjectResponse objectResponse = objectMetadataService.createObject(file.getOriginalFilename(), parentFolderId, file.getSize());
        String filePath = objectMetadataService.getObjectPath(objectResponse.getObjectId());
        objectResponse.setFilePath(filePath);
        log.info("Created object record for file: {}", file.getOriginalFilename());

        return objectResponse;
    }

    public UploadToken generateUploadToken(UUID objectId) {
        // Generate upload token
        UploadToken uploadToken = UploadTokenGenerator.generateUploadToken(objectId);
        log.info("Generated upload token: {}", uploadToken.getUploadToken());
        return uploadToken;
    }

}
