package org.brain.uploadservice.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public interface S3Repository {

    Void uploadToStagingBucket(MultipartFile file, String token);
}
