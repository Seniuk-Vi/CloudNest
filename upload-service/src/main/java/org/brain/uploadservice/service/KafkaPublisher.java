package org.brain.uploadservice.service;

import lombok.AllArgsConstructor;
import org.brain.uploadservice.configuration.KafkaProducerConfiguration;
import org.brain.uploadservice.model.UploadToken;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KafkaPublisher {

    private final KafkaTemplate<String, UploadToken> kafkaTemplate;

    public void publishCompressionMessage(UploadToken token) {
        kafkaTemplate.send(KafkaProducerConfiguration.FILE_COMPRESSION_TOPIC, token);
    }
}