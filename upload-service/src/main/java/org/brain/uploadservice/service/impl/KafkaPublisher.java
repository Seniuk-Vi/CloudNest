package org.brain.uploadservice.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.brain.uploadservice.configuration.KafkaProducerConfiguration;
import org.brain.uploadservice.exception.KafkaPublishMessageFailed;
import org.brain.uploadservice.model.UploadToken;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor
public class KafkaPublisher {

    private final KafkaTemplate<String, UploadToken> kafkaTemplate;

    private final KafkaProducerConfiguration kafkaProducerConfiguration;

    public void publishCompressionMessage(UploadToken token, String groupId) throws KafkaPublishMessageFailed {

        try {
            kafkaTemplate.send(kafkaProducerConfiguration.fileCompressionTopic, groupId, token).get(10, TimeUnit.SECONDS);
            log.info("Published message to Kafka for token: {}", token);
        }
        catch (Exception e) {
            log.error("Failed to publish message to Kafka", e);
            throw new KafkaPublishMessageFailed(e);
        }
    }

}