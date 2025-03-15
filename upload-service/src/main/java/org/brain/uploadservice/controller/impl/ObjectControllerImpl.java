package org.brain.uploadservice.controller.impl;

import lombok.AllArgsConstructor;
import org.brain.uploadservice.controller.ObjectController;
import org.brain.uploadservice.payload.ObjectResponse;
import org.brain.uploadservice.service.ObjectMetadataService;
import org.brain.uploadservice.service.UploadService;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
public class ObjectControllerImpl implements ObjectController {

    private final UploadService uploadService;

    private final ObjectMetadataService objectMetadataService;

    @Override
    public Resource getObjectById(Long objectId) {
        return null;
    }

    @Override
    public ObjectResponse getObjectMetadataById(Long objectId) {
        return objectMetadataService.getObjectById(objectId);
    }

    @Override
    public ObjectResponse createObject(Long parentFolderId, MultipartFile file) {
        return uploadService.handleFileUpload(file, parentFolderId);
    }


}
