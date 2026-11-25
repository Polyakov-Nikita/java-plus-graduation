package ru.practicum.explorewithme.common.kafka.consumer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

@ConfigurationProperties("kafka.consumer")
@RequiredArgsConstructor
public class ConsumerProperties {
    @Getter
    private final List<String> topics;
    @Getter
    private final Properties properties;
    private final long attemptTimeoutMillis;

    public Duration getAttemptTimeout() {
        return Duration.ofMillis(attemptTimeoutMillis);
    }
}
