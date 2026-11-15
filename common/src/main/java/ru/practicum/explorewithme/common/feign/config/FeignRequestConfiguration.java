package ru.practicum.explorewithme.common.feign.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Configuration
@SuppressWarnings("unused")
public class FeignRequestConfiguration {
    private static final String HEADER_NAME = "X-Request-Id";

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            String requestId = getCurrentRequestId();
            template.header(HEADER_NAME, requestId);
        };
    }

    private String getCurrentRequestId() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return UUID.randomUUID().toString();
        }
        HttpServletRequest request = attributes.getRequest();
        String requestId = request.getHeader(HEADER_NAME);
        if (requestId == null || requestId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return requestId;
    }
}
