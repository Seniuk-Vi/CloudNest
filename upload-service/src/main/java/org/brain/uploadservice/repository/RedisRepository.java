package org.brain.uploadservice.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository {

    void createObjectRecord(String token, String status);

    void updateStatus(String token, String status);
}
