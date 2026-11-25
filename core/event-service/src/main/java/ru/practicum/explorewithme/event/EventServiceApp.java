package ru.practicum.explorewithme.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import ru.practicum.explorewithme.stats.client.RecommendationsClient;

@SpringBootApplication
@EnableFeignClients
@ConfigurationPropertiesScan
@Import(RecommendationsClient.class)
public class EventServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EventServiceApp.class, args);
    }
}
