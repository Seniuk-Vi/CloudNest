package org.brain.uploadservice.service;

import lombok.AllArgsConstructor;
import org.brain.uploadservice.mapper.FolderMapper;
import org.brain.uploadservice.model.Folder;
import org.brain.uploadservice.payload.FolderResponse;
import org.brain.uploadservice.payload.NestResponse;
import org.brain.uploadservice.repository.FolderRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NestService {

    private final FolderRepository folderRepository;

    private final FolderMapper folderMapper;

    private final Boolean IS_ROOT = true;

    /**
     * Creates a base folder for new User with userId as folder name
     * @param userId User Id
     * @return
     */
    public NestResponse createNest(Long userId) {
        // check if user already has a root folder
        if (folderRepository.existsByUserIdAndIsRoot(userId, IS_ROOT)) {
            throw new RuntimeException("User already has a root folder");
        }
        Folder folder = new Folder(userId, IS_ROOT, userId.toString());
        Folder folderResponse = folderRepository.save(folder);
        return folderMapper.folderToNestResponse(folderResponse);
    }

    public NestResponse getNest(Long userId) {
        Folder folder = folderRepository.findByUserIdAndIsRoot(userId, IS_ROOT)
                .orElseThrow(() -> new RuntimeException("User does not have a root folder"));
        return folderMapper.folderToNestResponse(folder);
    }

}
