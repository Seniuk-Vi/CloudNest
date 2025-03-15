package org.brain.uploadservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ObjectResponse {
    private Long objectId;

    private String name;

    private Long size;

    private LocalDateTime createdAt;

    private String status;

    private String error;

    private String uploadToken;
}
