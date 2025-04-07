package org.brain.compressionworker.service.impl;

import org.brain.compressionworker.service.TempFileService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class TempFileServiceimpl implements TempFileService {

    private final String TEMP_DIR = "/temp";
    private final String COMPRESSION_FILE_POSTFIX = ".zst";

    @Override
    public File createTempFile(String filePath) throws IOException {
        File tempFile = new File(TEMP_DIR, filePath);
        if (!tempFile.exists()) {
            if (!tempFile.getParentFile().exists() && !tempFile.getParentFile().mkdirs()) {
                throw new IOException("Failed to create directory: " + tempFile.getParent());
            }
            if (!tempFile.createNewFile()) {
                throw new IOException("Failed to create temporary file: " + tempFile.getAbsolutePath());
            }
        }
        return tempFile;
    }

    @Override
    public File createCompressionTempFile(String filePath) throws IOException {
        File compressionTempFile = new File(TEMP_DIR, filePath + COMPRESSION_FILE_POSTFIX);
        if (!compressionTempFile.exists()) {
            if (!compressionTempFile.getParentFile().exists() && !compressionTempFile.getParentFile().mkdirs()) {
                throw new IOException("Failed to create directory: " + compressionTempFile.getParent());
            }
            if (!compressionTempFile.createNewFile()) {
                throw new IOException("Failed to create compression temporary file: " + compressionTempFile.getAbsolutePath());
            }
        }
        return compressionTempFile;
    }

    @Override
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void deleteOutdatedTempFiles() throws IOException {
        File tempDirectory = new File(TEMP_DIR);
        if (!tempDirectory.exists() || !tempDirectory.isDirectory()) {
            throw new IOException("Temporary directory does not exist or is not a directory: " + TEMP_DIR);
        }

        long oneHourAgo = System.currentTimeMillis() - (60 * 60 * 1000); // 1 hour in milliseconds

        // Iterate through all files in the temp directory recursively
        deleteOutdatedFilesRecursively(tempDirectory, oneHourAgo);
    }

    @Override
    public boolean deleteTempFile(File file) throws IOException {
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Failed to delete temporary file: " + file.getAbsolutePath());
            }
            return true;
        }
        return false;
    }

    private void deleteOutdatedFilesRecursively(File directory, long lastModified) throws IOException {
        File[] files = directory.listFiles();

        if (files == null) {
            throw new IOException("Failed to list files in directory: " + directory.getAbsolutePath());
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively handle subdirectories
                deleteOutdatedFilesRecursively(file, lastModified);
            } else {
                // Check the last modified timestamp
                if (file.lastModified() < lastModified) {
                    if (!file.delete()) {
                        throw new IOException("Failed to delete outdated file: " + file.getAbsolutePath());
                    }
                }
            }
        }

        // Delete the directory itself if it's empty after cleaning up
        if (directory.listFiles() != null && directory.listFiles().length == 0) {
            if (!directory.delete()) {
                throw new IOException("Failed to delete empty directory: " + directory.getAbsolutePath());
            }
        }
    }
}
