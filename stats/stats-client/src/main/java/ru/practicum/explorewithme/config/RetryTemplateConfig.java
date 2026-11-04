package ru.practicum.explorewithme.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.MaxAttemptsRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@SuppressWarnings("unused")
public class RetryTemplateConfig {
    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate template = new RetryTemplate();
        template.setBackOffPolicy(fixedBackOffPolicy());
        template.setRetryPolicy(maxAttemptsRetryPolicy());
        return template;
    }

    private FixedBackOffPolicy fixedBackOffPolicy() {
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000L);
        return fixedBackOffPolicy;
    }

    private MaxAttemptsRetryPolicy maxAttemptsRetryPolicy() {
        MaxAttemptsRetryPolicy maxAttemptsRetryPolicy = new MaxAttemptsRetryPolicy();
        maxAttemptsRetryPolicy.setMaxAttempts(3);
        return maxAttemptsRetryPolicy;
    }
}
