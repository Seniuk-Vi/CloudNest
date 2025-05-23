package org.brain.uploadservice.configuration;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.brain.uploadservice.model.UploadToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

@Configuration
public class KafkaProducerConfiguration {


    @Value(value = "${spring.kafka.topic.file-compression}")
    public String fileCompressionTopic;

    @Bean
    public ProducerFactory<String, UploadToken> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> configProps = kafkaProperties.buildProducerProperties();
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                JsonSerializer.class);
        configProps.put(
                JsonSerializer.ADD_TYPE_INFO_HEADERS,
                false);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, UploadToken> kafkaTemplate(ProducerFactory<String, UploadToken> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
