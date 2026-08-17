package com.example.oulearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class OulearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(OulearningApplication.class, args);
    }
}
