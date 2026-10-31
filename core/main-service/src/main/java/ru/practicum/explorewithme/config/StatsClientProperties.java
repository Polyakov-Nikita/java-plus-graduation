package ru.practicum.explorewithme.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@RequiredArgsConstructor
@ConfigurationProperties(prefix = "stats")
@Getter
public class StatsClientProperties {
    private final String serverName;
}
