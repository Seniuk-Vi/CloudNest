package org.brain.uploadservice.model;

import lombok.Getter;
import lombok.Setter;
import lombok.With;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;

import java.time.LocalDateTime;

@Node("Object")
@Getter
@Setter
public class FileObject {
    @Id
    @With
    @GeneratedValue
    private Long objectId;

    private Long userId;

    private String name;

    private Long size;

    @CreatedDate
    private LocalDateTime createdAt;

    private ObjectStatus status;

    private String error;


    public FileObject(Long userId, String name, Long size) {
        this(null, userId, name, size, null, null, null);
    }

    private FileObject(Long objectId, Long userId, String name, Long size, LocalDateTime createdAt, ObjectStatus status, String error) {
        this.objectId = objectId;
        this.userId = userId;
        this.name = name;
        this.size = size;
        this.createdAt = createdAt;
        this.status = status;
        this.error = error;
    }
}