package org.brain.uploadservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("UploadToken")
@Data
@AllArgsConstructor
public class UploadToken {
    @Id
    private String uploadToken;

    Long objectId;

    TokenStatus status;

    int percentage;

    String error;

    @TimeToLive
    Long ttl;

    public UploadToken(String uploadToken, Long objectId) {
        this.uploadToken = uploadToken;
        this.objectId = objectId;
        this.status = null;
        this.percentage = 0;
        this.error = null;
    }
}
