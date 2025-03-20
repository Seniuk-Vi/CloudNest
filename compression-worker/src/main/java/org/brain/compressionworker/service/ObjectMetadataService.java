package org.brain.compressionworker.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.brain.uploadservice.mapper.ObjectMapper;
import org.brain.uploadservice.model.FileObject;
import org.brain.uploadservice.model.Folder;
import org.brain.uploadservice.model.ObjectStatus;
import org.brain.uploadservice.payload.ObjectResponse;
import org.brain.uploadservice.repository.neo4j.FolderRepository;
import org.brain.uploadservice.repository.neo4j.ObjectRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ObjectMetadataService {


    private final ObjectRepository objectRepository;
    private final FolderRepository folderRepository;
    private final ObjectMapper objectMapper;


    public ObjectResponse getObjectById(UUID objectId) {
        return objectMapper.toObjectResponse(objectRepository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("Object not found")));
    }

    @Transactional
    public ObjectResponse createObject(String name, UUID parentFolderId, Long size) {
        Folder parentFolder = folderRepository.findById(parentFolderId)
                .orElseThrow(() -> new RuntimeException("Parent folder not found"));

        FileObject fileObject = new FileObject(parentFolder.getUserId(), name, size);
        fileObject.setStatus(ObjectStatus.UPLOADING);
        fileObject = objectRepository.save(fileObject);

        parentFolder.getFileObjects().add(fileObject);
        folderRepository.save(parentFolder);

        return objectMapper.toObjectResponse(fileObject);
    }

    public void updateStatus(UUID objectId, ObjectStatus status, String error) {
        FileObject fileObject = objectRepository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("Object not found"));
        fileObject.setStatus(status);
        fileObject.setError(error);
        objectRepository.save(fileObject);
    }

}
