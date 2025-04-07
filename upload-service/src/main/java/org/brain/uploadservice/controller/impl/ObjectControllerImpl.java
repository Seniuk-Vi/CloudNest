package org.brain.uploadservice.controller.impl;

import lombok.AllArgsConstructor;
import org.brain.uploadservice.controller.ObjectController;
import org.brain.uploadservice.payload.ObjectResponse;
import org.brain.uploadservice.service.UploadService;
import org.brain.uploadservice.service.impl.ObjectMetadataService;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@AllArgsConstructor
@RestController
public class ObjectControllerImpl implements ObjectController {

    private final UploadService uploadService;

    private final ObjectMetadataService objectMetadataService;

    @Override
    public Resource getObjectById(UUID objectId) {
        return null;
    }

    @Override
    public ObjectResponse getObjectMetadataById(UUID objectId) {
        return objectMetadataService.getObjectById(objectId);
    }

    @Override
    public ObjectResponse createObject(UUID parentFolderId, MultipartFile file) {
        return uploadService.handleFileUpload(file, parentFolderId);
    }


}
