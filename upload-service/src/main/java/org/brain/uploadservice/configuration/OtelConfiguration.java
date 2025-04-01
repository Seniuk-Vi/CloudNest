package org.brain.uploadservice.configuration;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtelConfiguration {

    @Value("${spring.application.name}")
    private String serviceName;
    @Bean
    public Tracer tracer() {
        // Retrieve the global tracer from OpenTelemetry
        return GlobalOpenTelemetry.getTracer(serviceName, "1.0.0");
    }
}
