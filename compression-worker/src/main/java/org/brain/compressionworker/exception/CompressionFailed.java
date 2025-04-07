package org.brain.compressionworker.exception;

public class CompressionFailed extends Exception {

    private final static String message = "Compression failed";

    public CompressionFailed(Exception e) {
        super(message, e);
    }
}
