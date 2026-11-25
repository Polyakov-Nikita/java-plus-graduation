package ru.practicum.explorewithme.analyzer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.practicum.explorewithme.common.kafka.starter.ServiceStarter;

@SpringBootApplication
public class AnalyzerServiceApp {
    private static final String STARTER_NAME_USER_ACTIONS = "userActionsStarter";
    private static final String STARTER_NAME_EVENTS_SIMILARITY = "eventsSimilarityStarter";

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(AnalyzerServiceApp.class, args);
        Thread userActionsThread = createStarterThread(context, STARTER_NAME_USER_ACTIONS);
        Thread eventSimilarityThread = createStarterThread(context, STARTER_NAME_EVENTS_SIMILARITY);
        userActionsThread.start();
        eventSimilarityThread.start();
    }

    private static Thread createStarterThread(ConfigurableApplicationContext context, String starterName) {
        ServiceStarter starter = context.getBean(starterName, ServiceStarter.class);
        return new Thread(starter::start, starterName + "Thread");
    }
}
