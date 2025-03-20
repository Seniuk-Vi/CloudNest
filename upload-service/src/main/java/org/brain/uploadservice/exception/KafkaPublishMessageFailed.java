package org.brain.uploadservice.exception;

public class KafkaPublishMessageFailed extends Exception {

    private final static String message = "Failed to publish message to Kafka";

    public KafkaPublishMessageFailed(Exception e) {
        super(message, e);
    }

    public KafkaPublishMessageFailed() {
        super(message);
    }

    public KafkaPublishMessageFailed(Throwable e) {
        super(message, e);
    }
}
