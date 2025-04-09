package org.brain.compressionworker.configuration;

import io.github.mweirauch.micrometer.jvm.extras.ProcessMemoryMetrics;
import io.github.mweirauch.micrometer.jvm.extras.ProcessThreadMetrics;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.micrometer.v1_5.OpenTelemetryMeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

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

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
    // array of SLA in nanoseconds from 1 to 89 in fibonacci sequence
    private static final double[] SLA = {
            TimeUnit.SECONDS.toNanos(1),
            TimeUnit.SECONDS.toNanos(5),
            TimeUnit.SECONDS.toNanos(10),
            TimeUnit.SECONDS.toNanos(20),
            TimeUnit.SECONDS.toNanos(30),
            TimeUnit.SECONDS.toNanos(40),
            TimeUnit.SECONDS.toNanos(50),
            TimeUnit.SECONDS.toNanos(60),
            TimeUnit.SECONDS.toNanos(70),
            TimeUnit.SECONDS.toNanos(80),

    };
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> configureMetrics() {
        return registry -> registry.config().meterFilter(
                new MeterFilter() {
                    @Override
                    public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                        if(id.getName().startsWith("compression")) {
                            return DistributionStatisticConfig.builder()
                                    .percentiles(0.95)
                                    .percentilesHistogram(true)
                                    .serviceLevelObjectives(SLA)
                                    .build()
                                    .merge(config);
                        }
                        return config;
                    }
                });
    }
}
