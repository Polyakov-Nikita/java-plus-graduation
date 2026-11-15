package ru.practicum.explorewithme.compilations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CompilationsServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CompilationsServiceApp.class, args);
    }
}
