package org.brain.compressionworker.repository.neo4j;

import org.brain.uploadservice.model.Folder;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FolderRepository extends Neo4jRepository<Folder, UUID> {

    Boolean existsByUserIdAndIsRoot(UUID userId, Boolean isRoot);
    Optional<Folder> findByUserIdAndIsRoot(UUID userId, Boolean isRoot);
}
