package org.brain.uploadservice.controller.impl;

import lombok.AllArgsConstructor;
import org.brain.uploadservice.controller.FolderController;
import org.brain.uploadservice.payload.FolderResponse;
import org.brain.uploadservice.service.impl.FolderService;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@AllArgsConstructor
@RestController
public class FolderControllerImpl implements FolderController {

    private final FolderService folderService;

    @Override
    public FolderResponse getRootFolder(UUID userId) {
        return folderService.getRootFolder(userId);
    }

    @Override
    public FolderResponse getFolderById(UUID folderId) {
        return folderService.getFolderById(folderId);
    }

    @Override
    public FolderResponse createFolder(String name, UUID parentFolderId) {
        return folderService.createFolder(name, parentFolderId);
    }
}
