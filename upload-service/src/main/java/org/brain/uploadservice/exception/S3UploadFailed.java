package org.brain.uploadservice.exception;

public class S3UploadFailed extends Exception {

    private static final String MESSAGE = "S3 upload failed to Staging bucket";

    public S3UploadFailed(Exception e) {
        super(MESSAGE, e);
    }
}
