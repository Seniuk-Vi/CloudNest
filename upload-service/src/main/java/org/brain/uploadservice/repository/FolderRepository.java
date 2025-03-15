package org.brain.uploadservice.repository;

import org.brain.uploadservice.model.Folder;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FolderRepository extends Neo4jRepository<Folder, Long> {

    Boolean existsByUserIdAndIsRoot(Long userId, Boolean isRoot);
    Optional<Folder> findByUserIdAndIsRoot(Long userId, Boolean isRoot);
}
