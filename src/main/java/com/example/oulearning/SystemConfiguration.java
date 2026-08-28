package com.example.oulearning;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SystemConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
