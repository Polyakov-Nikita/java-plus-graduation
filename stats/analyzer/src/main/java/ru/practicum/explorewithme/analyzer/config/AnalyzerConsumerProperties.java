package ru.practicum.explorewithme.analyzer.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.practicum.explorewithme.common.kafka.consumer.ConsumerProperties;

@ConfigurationProperties("kafka.consumer")
@RequiredArgsConstructor
@Getter
public class AnalyzerConsumerProperties {
    private final ConsumerProperties userActions;
    private final ConsumerProperties eventsSimilarity;
}
