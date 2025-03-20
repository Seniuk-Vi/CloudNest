package org.brain.compressionworker.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.brain.uploadservice.mapper.FolderMapper;
import org.brain.uploadservice.model.Folder;
import org.brain.uploadservice.payload.FolderResponse;
import org.brain.uploadservice.repository.neo4j.FolderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class FolderService {

    private final FolderRepository folderRepository;

    private final FolderMapper folderMapper;

    public FolderResponse getRootFolder(UUID userId) {
        return folderMapper.toFolderResponse(folderRepository.findByUserIdAndIsRoot(userId, true)
                .orElseThrow(() -> new RuntimeException("Root folder not found")));
    }

    public FolderResponse getFolderById(UUID folderId) {
        return folderMapper.toFolderResponse(folderRepository.findById(folderId)
                .orElseThrow(() -> new RuntimeException("Folder not found")));
    }

    @Transactional
    public FolderResponse createFolder(String name, UUID parentFolderId) {
        Folder parentFolder = folderRepository.findById(parentFolderId)
                .orElseThrow(() -> new RuntimeException("Parent folder not found"));

        Folder folder = new Folder(parentFolder.getUserId(), false, name);
        folder = folderRepository.save(folder);

        parentFolder.getChildFolders().add(folder);
        folderRepository.save(parentFolder);

        return folderMapper.toFolderResponse(folder);
    }

}
