package org.brain.compressionworker.repository.neo4j;

import org.brain.compressionworker.model.FileObject;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ObjectRepository extends Neo4jRepository<FileObject, UUID> {

}
