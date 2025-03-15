package org.brain.uploadservice.repository;

import org.brain.uploadservice.model.FileObject;
import org.brain.uploadservice.model.Folder;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObjectRepository extends Neo4jRepository<FileObject, Long> {

}
