package org.brain.compressionworker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.brain.uploadservice.model.TokenStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;

@RedisHash("UploadToken")
@Data
@AllArgsConstructor
public class UploadToken {
    @Id
    private String uploadToken;

    UUID objectId;

    TokenStatus status;

    int percentage;

    String error;

    @TimeToLive
    Long ttl;

    public UploadToken(String uploadToken, UUID objectId) {
        this.uploadToken = uploadToken;
        this.objectId = objectId;
        this.status = null;
        this.percentage = 0;
        this.error = null;
    }
}
