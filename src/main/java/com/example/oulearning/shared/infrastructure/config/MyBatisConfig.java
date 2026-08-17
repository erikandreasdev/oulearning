package com.example.oulearning.shared.infrastructure.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis mapper scanning configuration for infrastructure persistence packages across bounded contexts.
 */
@Configuration
@MapperScan(basePackages = {
    "com.example.oulearning.organization.infrastructure.persistence",
    "com.example.oulearning.budgeting.infrastructure.persistence",
    "com.example.oulearning.training.infrastructure.persistence"
})
public class MyBatisConfig {}
