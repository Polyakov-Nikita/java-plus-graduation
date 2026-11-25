package ru.practicum.explorewithme.request;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import ru.practicum.explorewithme.stats.client.RecommendationsClient;

@SpringBootApplication
@EnableFeignClients
@Import(RecommendationsClient.class)
public class RequestServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(RequestServiceApp.class, args);
    }
}
