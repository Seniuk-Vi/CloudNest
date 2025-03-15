package org.brain.uploadservice.model;

public enum TokenStatus {
    STAGING("Staging"),
    WAITING_FOR_COMPRESSING("Waiting for Compressing"),
    COMPRESSING("Compressing"),
    UPLOADED("Uploaded"),
    FAILED("Failed");

    private final String description;

    TokenStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}