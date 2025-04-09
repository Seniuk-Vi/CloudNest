package org.brain.compressionworker;

import org.brain.compressionworker.configuration.ApplicationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableConfigurationProperties(ApplicationProperties.class)
@EnableAspectJAutoProxy

public class CompressionWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CompressionWorkerApplication.class, args);
    }

}
