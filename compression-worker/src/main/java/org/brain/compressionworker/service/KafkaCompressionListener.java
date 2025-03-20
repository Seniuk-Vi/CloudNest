package org.brain.compressionworker.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.compressionworker.model.UploadToken;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@AllArgsConstructor
public class KafkaCompressionListener {

    @KafkaListener(topics = "${spring.kafka.topic.file-compression}")
    public void fileCompressionListener(UploadToken message) {
        log.info("Received message: {}", message);
    }

}