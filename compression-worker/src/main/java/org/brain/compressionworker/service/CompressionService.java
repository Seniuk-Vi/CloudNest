package org.brain.compressionworker.service;

import com.github.luben.zstd.Zstd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CompressionService {

    public byte[] compress(byte[] data) {
        // log time taken to compress im seconds
        long start = System.currentTimeMillis();
        byte[] compressed = Zstd.compress(data);
        log.info("Compression time: {} seconds", (System.currentTimeMillis() - start) / 1000);
        return compressed;
    }
}