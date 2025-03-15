package org.brain.uploadservice.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDateTime;
import java.util.List;

@Node("Folder")
@Getter
@Setter
@AllArgsConstructor
public class Folder {
    @Id
    @With
    @GeneratedValue
    private Long folderId;

    private Long userId;

    private Boolean isRoot;

    private String name;

    @CreatedDate
    private LocalDateTime createdAt;

    @Relationship(type = "HAS_CHILD_FOLDER", direction = Relationship.Direction.OUTGOING)
    private List<Folder> childFolders;

    @Relationship(type = "CONTAINS_OBJECT", direction = Relationship.Direction.OUTGOING)
    private List<FileObject> fileObjects;

    public Folder(Long userId, Boolean isRoot, String name) {
        this(null, userId, isRoot, name);
    }

    private Folder(Long folderId, Long userId, Boolean isRoot, String name) {
        this.folderId = folderId;
        this.userId = userId;
        this.isRoot = isRoot;
        this.name = name;
    }
}
