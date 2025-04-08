package org.brain.compressionworker.service;

import com.github.luben.zstd.Zstd;
import com.github.luben.zstd.ZstdOutputStream;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.Histogram;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.DoubleHistogram;
import io.opentelemetry.api.metrics.Meter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CompressionService {

    private final TempFileService tempFileService;
    private final Meter meter;
    private final DoubleHistogram compressionTimeHistogram;
    private final int COMPRESSION_LEVEL = 11;
    private final int COMPRESSION_WORKERS = 4;


    CompressionService(TempFileService tempFileService, OpenTelemetry openTelemetry) {
        this.tempFileService = tempFileService;
        // Retrieve the global Meter instance provided by the agent
        this.meter = openTelemetry.getMeter("compression-worker");

        // Define a custom Histogram metric for compression time in seconds
        this.compressionTimeHistogram = meter.histogramBuilder("compression_time")
                .setDescription("Time taken to compress files")
                .setUnit("seconds")
                .build();
    }

    public File compress(File inputFile, String filePath) throws IOException {
        long start = System.currentTimeMillis();
        try {
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
        } finally {
            double duration = (System.currentTimeMillis() - start) / 1000.; // Convert to seconds
            compressionTimeHistogram.record(duration); // Stop the timer
            log.info("Compression completed for file: {} in: {} seconds", inputFile.getAbsolutePath(), duration);
        }
    }
}