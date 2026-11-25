package ru.practicum.explorewithme.aggregator.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.explorewithme.aggregator.service.AggregatorService;
import ru.practicum.explorewithme.common.kafka.consumer.ConsumerProperties;
import ru.practicum.explorewithme.common.kafka.starter.ServiceStarter;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ConsumerProperties.class)
@SuppressWarnings("unused")
public class AggregatorStarterConfiguration {
    private final ConsumerProperties consumerProperties;
    private final AggregatorService aggregatorService;

    @Bean
    public ServiceStarter serviceStarter() {
        return new ServiceStarter(consumerProperties, aggregatorService);
    }
}
