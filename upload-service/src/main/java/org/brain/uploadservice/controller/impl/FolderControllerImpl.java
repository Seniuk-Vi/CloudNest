package org.brain.uploadservice.controller.impl;

import lombok.AllArgsConstructor;
import org.brain.uploadservice.controller.FolderController;
import org.brain.uploadservice.payload.FolderResponse;
import org.brain.uploadservice.service.FolderService;

@AllArgsConstructor
public class FolderControllerImpl implements FolderController {

    private final FolderService folderService;

    @Override
    public FolderResponse getRootFolder(Long userId) {
        return folderService.getRootFolder(userId);
    }

    @Override
    public FolderResponse getFolderById(Long folderId) {
        return folderService.getFolderById(folderId);
    }

    @Override
    public FolderResponse createFolder(String name, Long parentFolderId) {
        return folderService.createFolder(name, parentFolderId);
    }
}
