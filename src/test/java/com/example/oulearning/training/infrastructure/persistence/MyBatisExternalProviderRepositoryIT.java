package com.example.oulearning.training.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.training.domain.model.ExternalProvider;
import com.example.oulearning.training.domain.model.ExternalProviderId;
import com.example.oulearning.training.domain.model.ExternalProviderContact;
import com.example.oulearning.training.domain.model.ExternalProviderName;
import com.example.oulearning.organization.domain.employee.model.Email;
import com.example.oulearning.training.domain.model.Phone;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.junit.jupiter.api.DisplayName;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.OracleContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MyBatisExternalProviderRepository.class, FlywayAutoConfiguration.class})
@Testcontainers
class MyBatisExternalProviderRepositoryIT {

    @Container
    @ServiceConnection
    static OracleContainer oracle = new OracleContainer("gvenzl/oracle-xe:21-slim");

    @Autowired
    private MyBatisExternalProviderRepository providerRepository;

    @Test
    @DisplayName("given valid provider, when saving, then can be retrieved")
    void givenValidProvider_whenSaving_thenCanBeRetrieved() {
        // given
        final var provider = ExternalProvider.create(
                new ExternalProviderId(1L),
                new ExternalProviderName("Pluralsight"),
                new ExternalProviderContact(new Email("contact@pluralsight.com"), new Phone("12345")));

        // when
        providerRepository.save(provider);

        // then
        // Assuming ID is 1 for the first insert
        final var retrieved = providerRepository.findById(new ExternalProviderId(1L));

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().name().value()).isEqualTo("Pluralsight");
        assertThat(retrieved.get().active()).isTrue();
    }

    @Test
    @DisplayName("given existing provider, when updating name, then changes are persisted")
    @Sql(scripts = "/sql/insert-provider.sql")
    void givenExistingProvider_whenUpdatingName_thenChangesArePersisted() {
        // given
        final var providerId = new ExternalProviderId(2L);

        final var retrieved = providerRepository.findById(providerId).orElseThrow();

        retrieved.update(new ExternalProviderName("Udemy Business"), new ExternalProviderContact(new Email("contact@udemy.com"), new Phone("54321")));

        // when
        providerRepository.save(retrieved);

        // then
        final var updated = providerRepository.findById(providerId);
        assertThat(updated).isPresent();
        assertThat(updated.get().name().value()).isEqualTo("Udemy Business");
    }

    @Test
    @DisplayName("given active provider, when deactivating, then active flag is updated")
    @Sql(scripts = "/sql/insert-provider.sql")
    void givenActiveProvider_whenDeactivating_thenActiveFlagIsUpdated() {
        // given
        final var providerId = new ExternalProviderId(2L);
        final var retrieved = providerRepository.findById(providerId).orElseThrow();
        final var deactivatedProvider = retrieved.deactivate();

        // when
        providerRepository.save(deactivatedProvider);

        // then
        final var updated = providerRepository.findById(providerId);
        assertThat(updated).isPresent();
        assertThat(updated.get().active()).isFalse();
    }
}
