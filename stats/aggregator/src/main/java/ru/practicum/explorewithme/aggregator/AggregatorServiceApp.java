package ru.practicum.explorewithme.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.explorewithme.common.kafka.starter.ServiceStarter;

@SpringBootApplication
public class AggregatorServiceApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AggregatorServiceApp.class, args);
        ServiceStarter starter = context.getBean(ServiceStarter.class);
        starter.start();
    }
}
