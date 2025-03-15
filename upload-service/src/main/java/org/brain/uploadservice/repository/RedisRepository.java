package org.brain.uploadservice.repository;

import org.brain.uploadservice.model.UploadToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RedisRepository extends CrudRepository<UploadToken, String> {

}
