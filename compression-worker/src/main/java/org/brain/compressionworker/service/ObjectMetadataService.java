package org.brain.compressionworker.service;

import lombok.AllArgsConstructor;

import org.brain.compressionworker.model.FileObject;
import org.brain.compressionworker.model.ObjectStatus;
import org.brain.compressionworker.repository.neo4j.ObjectRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ObjectMetadataService {


    private final ObjectRepository objectRepository;

    public void updateStatus(UUID objectId, ObjectStatus status, String error) {
        FileObject fileObject = objectRepository.findById(objectId)
                .orElseThrow(() -> new RuntimeException("Object not found"));
        fileObject.setStatus(status);
        fileObject.setError(error);
        objectRepository.save(fileObject);
    }

}
