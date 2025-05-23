package org.brain.compressionworker.repository.redis;

import org.brain.compressionworker.model.UploadToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository extends CrudRepository<UploadToken, String> {

}
