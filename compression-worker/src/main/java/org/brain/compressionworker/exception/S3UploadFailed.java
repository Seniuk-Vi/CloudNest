package org.brain.compressionworker.exception;

public class S3UploadFailed extends Exception {

    private final static String message = "S3 upload failed to Main bucket";

    public S3UploadFailed(Exception e) {
        super(message, e);
    }

    public S3UploadFailed(String message) {
        super(message);
    }

}
