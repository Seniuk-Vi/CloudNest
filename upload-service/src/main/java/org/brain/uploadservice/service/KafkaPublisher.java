package org.brain.uploadservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaPublisher {

//    @Autowired
//    private KafkaTemplate<String, CompressionMessage> kafkaTemplate;

    public void publishCompressionMessage(String token) {
//        CompressionMessage message = new CompressionMessage(token, "START_COMPRESSION");
//        kafkaTemplate.send("FileCompressionTopic", message);
    }
}