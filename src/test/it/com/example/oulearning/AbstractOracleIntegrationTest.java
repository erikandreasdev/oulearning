package com.example.oulearning;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class AbstractOracleIntegrationTest {

    @Container
    protected static final GenericContainer<?> ORACLE_CONTAINER =
            new GenericContainer<>(DockerImageName.parse("gvenzl/oracle-free:23-slim-faststart"))
                    .withEnv("APP_USER", "test")
                    .withEnv("APP_USER_PASSWORD", "test")
                    .withEnv("ORACLE_PASSWORD", "test")
                    .withExposedPorts(1521)
                    .waitingFor(new LogMessageWaitStrategy()
                            .withRegEx(".*DATABASE IS READY TO USE!.*\\s"));

    @DynamicPropertySource
    static void oracleProperties(final DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                () -> "jdbc:oracle:thin:@"
                        + ORACLE_CONTAINER.getHost()
                        + ":"
                        + ORACLE_CONTAINER.getMappedPort(1521)
                        + "/FREEPDB1");
        registry.add("spring.datasource.username", () -> "test");
        registry.add("spring.datasource.password", () -> "test");
        registry.add(
                "spring.flyway.url",
                () -> "jdbc:oracle:thin:@"
                        + ORACLE_CONTAINER.getHost()
                        + ":"
                        + ORACLE_CONTAINER.getMappedPort(1521)
                        + "/FREEPDB1");
        registry.add("spring.flyway.user", () -> "test");
        registry.add("spring.flyway.password", () -> "test");
    }
}
