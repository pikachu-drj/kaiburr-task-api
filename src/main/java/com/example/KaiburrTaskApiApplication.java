package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // This single annotation enables Spring Boot
public class KaiburrTaskApiApplication {

    public static void main(String[] args) {
        // This line launches the entire application
        SpringApplication.run(KaiburrTaskApiApplication.class, args);
    }

}