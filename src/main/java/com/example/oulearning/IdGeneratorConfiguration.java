package com.example.oulearning;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGeneratorConfiguration {

    private final AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    @Bean
    public com.example.oulearning.budgeting.domain.model.IdGenerator budgetIdGenerator() {
        return counter::incrementAndGet;
    }

    @Bean
    public com.example.oulearning.organization.domain.employee.model.IdGenerator employeeIdGenerator() {
        return counter::incrementAndGet;
    }

    @Bean
    public com.example.oulearning.organization.domain.hierarchy.model.IdGenerator organizationalUnitIdGenerator() {
        return counter::incrementAndGet;
    }

    @Bean
    public com.example.oulearning.training.domain.model.IdGenerator trainingIdGenerator() {
        return counter::incrementAndGet;
    }
}
