package org.brain.compressionworker.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public interface TempFileService {

    File createTempFile(String filePath) throws IOException;

    File createCompressionTempFile(String filePath) throws IOException;

    boolean deleteTempFile(File file) throws IOException;

    void deleteOutdatedTempFiles() throws IOException;
}
