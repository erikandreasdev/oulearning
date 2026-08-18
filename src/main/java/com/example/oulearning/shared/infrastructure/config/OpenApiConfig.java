package com.example.oulearning.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.oulearning.organization.domain.organization.Organization;

/**
 * OpenAPI 3.0 configuration exposing documentation and Swagger UI at /swagger-ui.html.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OU Learning & Budgeting Management API")
                        .version("v1.0.0")
                        .description("""
                                Clean / Hexagonal Architecture REST API for managing Organizational Units, \
                                historical Organization snapshots with caching, and Budgeting lifecycle operations \
                                (allocation, reservation, consumption, and distribution).
                                """)
                        .contact(new Contact()
                                .name("Core Architecture Team")
                                .email("architecture@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")));
    }
}
