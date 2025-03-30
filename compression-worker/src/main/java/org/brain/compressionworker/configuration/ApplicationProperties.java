package org.brain.compressionworker.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring")
@Data
public class ApplicationProperties {

    private final Kafka kafka = new Kafka();
    private final Cloud cloud = new Cloud();

    @Data
    public static class Kafka {
        private String bootstrapServers;
        private final Topic topic = new Topic();

        @Data
        public static class Topic {
            private String fileCompression;
            private String fileCompressionGroupId;
        }
    }

    @Data
    public static class Cloud {
        private final Aws aws = new Aws();

        @Data
        public static class Aws {
            private final S3 s3 = new S3();

            @Data
            public static class S3 {
                private String stagingBucket;
                private String mainBucket;
            }
        }
    }
}