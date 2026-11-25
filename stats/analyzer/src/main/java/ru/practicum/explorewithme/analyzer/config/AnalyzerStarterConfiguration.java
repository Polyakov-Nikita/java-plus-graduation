package ru.practicum.explorewithme.analyzer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.explorewithme.analyzer.service.kafka.AnalyzerEventsSimilarityService;
import ru.practicum.explorewithme.analyzer.service.kafka.AnalyzerUserActionsService;
import ru.practicum.explorewithme.common.kafka.starter.ServiceStarter;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(AnalyzerConsumerProperties.class)
@SuppressWarnings("unused")
public class AnalyzerStarterConfiguration {
    private final AnalyzerConsumerProperties analyzerConsumerProperties;
    private final AnalyzerUserActionsService analyzerUserActionsService;
    private final AnalyzerEventsSimilarityService analyzerEventsSimilarityService;

    @Bean
    public ServiceStarter userActionsStarter() {
        return new ServiceStarter(analyzerConsumerProperties.getUserActions(), analyzerUserActionsService);
    }

    @Bean
    public ServiceStarter eventsSimilarityStarter() {
        return new ServiceStarter(analyzerConsumerProperties.getEventsSimilarity(), analyzerEventsSimilarityService);
    }
}
