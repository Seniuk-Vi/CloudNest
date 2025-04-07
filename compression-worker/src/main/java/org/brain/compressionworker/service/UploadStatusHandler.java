package org.brain.compressionworker.service;

import org.brain.compressionworker.model.ObjectStatus;
import org.brain.compressionworker.model.TokenStatus;
import org.brain.compressionworker.model.UploadToken;

public interface UploadStatusHandler {
    /**
     * Handles the failure of an upload.
     *
     * @param uploadToken The upload token associated with the failed upload.
     * @param errorMessage The error message describing the failure.
     * @param objectStatus The status to set for the object in the database.
     * @param tokenStatus The status to set for the upload token in the database.
     */
    void handleUploadStatus(UploadToken uploadToken, String errorMessage, ObjectStatus objectStatus, TokenStatus tokenStatus);

    /**
     * Handles UploadToken status update.
     *
     * @param uploadToken The upload token to update.
     * @param tokenStatus The new status to set for the upload token.
     * @param errorMessage The error message to set for the upload token, if any.
     */
    void handleUploadTokenStatus(UploadToken uploadToken, TokenStatus tokenStatus, String errorMessage);
}
