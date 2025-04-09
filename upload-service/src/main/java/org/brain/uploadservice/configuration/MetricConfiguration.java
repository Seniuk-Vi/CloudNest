package org.brain.uploadservice.configuration;

import io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics;
import io.github.mweirauch.micrometer.jvm.extras.ProcessThreadMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.micrometer.v1_5.OpenTelemetryMeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricConfiguration {

    @Bean
    public OpenTelemetry openTelemetry() {
        return GlobalOpenTelemetry.get();
    }

    @Bean
    public MeterRegistry meterRegistry(@Value("${spring.application.name}") String applicationName) {
        // Create a MeterRegistry with OpenTelemetry
        MeterRegistry meterRegistry = OpenTelemetryMeterRegistry.builder(GlobalOpenTelemetry.get()).build();
        // Add tags
        meterRegistry.config().commonTags("application", applicationName);
        return meterRegistry;
    }

    @Bean
    public MeterBinder processMemoryMetrics() {
        return new ProcessMemoryMetrics();
    }

    @Bean
    public MeterBinder processThreadMetrics() {
        return new ProcessThreadMetrics();
    }
}
