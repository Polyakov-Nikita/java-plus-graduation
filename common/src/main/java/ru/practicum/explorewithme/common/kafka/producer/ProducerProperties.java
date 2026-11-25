package ru.practicum.explorewithme.common.kafka.producer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.Properties;

@ConfigurationProperties("kafka.producer")
@RequiredArgsConstructor
public class ProducerProperties {
    @Getter
    private final Properties properties;
    @Getter
    private final String topic;
    private final int closeDurationSeconds;

    public Duration getCloseDuration() {
        return Duration.ofSeconds(closeDurationSeconds);
    }
}
