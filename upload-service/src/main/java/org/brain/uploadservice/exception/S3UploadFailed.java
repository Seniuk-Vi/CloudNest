package org.brain.uploadservice.exception;

public class S3UploadFailed extends Exception {

    private final static String message = "S3 upload failed";

    public S3UploadFailed(Exception e) {
        super(message, e);
    }
}
