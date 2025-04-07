package org.brain.compressionworker.configuration;

import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.brain.compressionworker.model.UploadToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class KafkaConsumerConfiguration {

    private final ApplicationProperties applicationProperties;

    @Bean
    public ConsumerFactory<String, UploadToken> consumerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                applicationProperties.getKafka().getBootstrapServers());
        configProps.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        configProps.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);
        configProps.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                applicationProperties.getKafka().getTopic().getFileCompressionGroupId());
        configProps.put(
                JsonDeserializer.TRUSTED_PACKAGES,
                "*");
        // enable manual acknowledgment
        configProps.put(
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                false);

        ConsumerFactory<String, UploadToken> consumerFactory = new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), new JsonDeserializer<>(UploadToken.class));
        return consumerFactory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UploadToken>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UploadToken> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
