package org.brain.uploadservice.service;

import org.brain.uploadservice.payload.ObjectResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface UploadService {
    ObjectResponse handleFileUpload(MultipartFile file, UUID parentFolderId);
}
