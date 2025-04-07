package org.brain.uploadservice.service;


import org.brain.uploadservice.exception.S3UploadFailed;

public interface S3Service {

    /**
     * Uploads a file to S3 using multipart upload.
     *
     * @param file      The file as a byte array.
     * @param key       The S3 object key.
     * @throws S3UploadFailed If the upload fails.
     */
    void uploadToStagingBucket(byte[] file, String key) throws S3UploadFailed;
}