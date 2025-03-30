package org.brain.uploadservice.repository.neo4j;

import org.brain.uploadservice.model.FileObject;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ObjectRepository extends Neo4jRepository<FileObject, UUID> {


    @Query("""
                MATCH (folder:Folder)-[:CONTAINS_OBJECT]->(file:Object {objectId: $objectId})
                OPTIONAL MATCH path = (folder)<-[:HAS_CHILD_FOLDER*0..]-(root:Folder {isRoot: true})
                RETURN
                    CASE
                        WHEN path IS NOT NULL
                        THEN reduce(fullPath = "", n IN reverse(nodes(path)) | fullPath + "/" + n.name) + "/" + file.name
                        ELSE "/" + file.name
                    END AS fullFilePath
            """)
    Optional<String> findFullFilePathByObjectId(UUID objectId);
}
