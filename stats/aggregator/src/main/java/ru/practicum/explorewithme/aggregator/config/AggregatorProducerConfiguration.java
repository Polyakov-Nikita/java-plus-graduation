package ru.practicum.explorewithme.aggregator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.explorewithme.common.kafka.producer.ProducerProperties;
import ru.practicum.explorewithme.common.kafka.producer.StatsProducer;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ProducerProperties.class)
@SuppressWarnings("unused")
public class AggregatorProducerConfiguration {
    private final ProducerProperties producerProperties;

    @Bean
    public StatsProducer statsProducer() {
        return new StatsProducer(producerProperties);
    }
}
