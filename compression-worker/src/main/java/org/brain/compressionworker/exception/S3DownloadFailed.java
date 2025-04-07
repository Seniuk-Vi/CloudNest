package org.brain.compressionworker.exception;

public class S3DownloadFailed extends Exception {

    private final static String message = "S3 download failed to Main bucket";

    public S3DownloadFailed(Exception e) {
        super(message, e);
    }
}
