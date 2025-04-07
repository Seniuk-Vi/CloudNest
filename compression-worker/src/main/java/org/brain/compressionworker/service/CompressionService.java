package org.brain.compressionworker.service;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdOutputStream;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
@Slf4j
@AllArgsConstructor
public class CompressionService {

    private final TempFileService tempFileService;

    private final int COMPRESSION_LEVEL = 11;
    private final int COMPRESSION_WORKERS = 4;

    public File compress(File inputFile, String filePath) throws IOException {
        try {

            long start = System.currentTimeMillis();

            // Create a temporary file for storing compressed data
            File tempFile = tempFileService.createCompressionTempFile(filePath);
            tempFile.deleteOnExit(); // Delete temporary file on JVM exit

            try (FileInputStream fis = new FileInputStream(inputFile);
                 FileOutputStream fos = new FileOutputStream(tempFile);
                 ZstdOutputStream zos = new ZstdOutputStream(fos)) {
                zos.setLevel(COMPRESSION_LEVEL);
                zos.setWorkers(COMPRESSION_WORKERS);
                byte[] buffer = new byte[8192]; // 8 KB buffer
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    zos.write(buffer, 0, bytesRead);
                }
            }

            log.info("Compression time: {} seconds", (System.currentTimeMillis() - start) / 1000);

            // Replace the original file with the compressed file
            tempFileService.deleteTempFile(inputFile);
            if (!tempFile.renameTo(inputFile)) {
                throw new IOException("Failed to rename temporary file to original file: " + inputFile.getAbsolutePath());
            }
            return inputFile;
        } catch (IOException e) {
            log.error("Compression failed for file: {}", inputFile.getAbsolutePath(), e);
            // delete tempFile
            if (tempFileService.deleteTempFile(inputFile)) {
                log.error("Failed to delete temporary file: {}", inputFile.getAbsolutePath());
            }
            throw e;
        }
    }
}