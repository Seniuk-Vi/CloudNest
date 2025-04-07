package org.brain.compressionworker.service;


import org.brain.compressionworker.exception.S3UploadFailed;

import java.io.File;

public interface S3Service {

    /**
     * Uploads a file to S3 using multipart upload.
     *
     * @param file      The file as a byte array.
     * @param key       The S3 object key.
     * @throws S3UploadFailed If the upload fails.
     */
    public void uploadToMainBucket(File file, String key) throws S3UploadFailed;
}