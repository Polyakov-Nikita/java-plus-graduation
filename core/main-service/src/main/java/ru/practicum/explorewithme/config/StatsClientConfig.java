package ru.practicum.explorewithme.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import ru.practicum.explorewithme.StatsClient;

@Configuration
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class StatsClientConfig {
    private final StatsClientProperties clientProperties;

    @Bean
    public StatsClient statsClient(RestTemplateBuilder builder, DiscoveryClient discoveryClient, RetryTemplate retryTemplate) {
        return new StatsClient(clientProperties.getServerName(), builder, discoveryClient, retryTemplate);
    }
}
