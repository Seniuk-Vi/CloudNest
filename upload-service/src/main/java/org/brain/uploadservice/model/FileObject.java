package org.brain.uploadservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.RelationshipId;

import java.time.LocalDateTime;
import java.util.UUID;

@Node("Object")
@Getter
@Setter
@NoArgsConstructor
public class FileObject {
    @Id
    @GeneratedValue
    private UUID objectId;

    private UUID userId;

    private String name;

    private Long size;

    @CreatedDate
    private LocalDateTime createdAt;

    private ObjectStatus status;

    private String error;

    public FileObject(UUID userId, String name, Long size) {
        this(null, userId, name, size, null, null, null);
    }

    private FileObject(UUID objectId, UUID userId, String name, Long size, LocalDateTime createdAt, ObjectStatus status, String error) {
        this.objectId = objectId;
        this.userId = userId;
        this.name = name;
        this.size = size;
        this.createdAt = createdAt;
        this.status = status;
        this.error = error;
    }
}