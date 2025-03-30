package org.brain.compressionworker;

import org.brain.compressionworker.configuration.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
public class CompressionWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompressionWorkerApplication.class, args);
    }

}
