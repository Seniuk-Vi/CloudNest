package org.brain.uploadservice.model;

public enum ObjectStatus {
    UPLOADING("Uploading"),
    UPLOADED("Uploaded"),
    FAILED("Failed");

    private final String description;

    ObjectStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}