package com.example.oulearning.training.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.oulearning.AbstractOracleIntegrationTest;
import com.example.oulearning.organization.domain.employee.model.Email;
import com.example.oulearning.training.domain.model.ExternalProvider;
import com.example.oulearning.training.domain.model.ExternalProviderContact;
import com.example.oulearning.training.domain.model.ExternalProviderId;
import com.example.oulearning.training.domain.model.ExternalProviderName;
import com.example.oulearning.training.domain.model.Phone;
import com.example.oulearning.training.domain.model.TrainingTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MyBatisExternalProviderRepository.class, FlywayAutoConfiguration.class})
class MyBatisExternalProviderRepositoryIT extends AbstractOracleIntegrationTest {

    @Autowired
    private MyBatisExternalProviderRepository providerRepository;

    @Test
    @DisplayName("given valid provider, when saving, then can be retrieved")
    void givenValidProvider_whenSaving_thenCanBeRetrieved() {
        // given
        final var randomName = TrainingTestFactory.randomTrainingNameString();
        final var randomEmail = "contact@example.com";
        final var provider = ExternalProvider.create(
                new ExternalProviderId(1L),
                new ExternalProviderName(randomName),
                new ExternalProviderContact(new Email(randomEmail), new Phone("1234567")));

        // when
        providerRepository.save(provider);

        // then
        // Assuming ID is 1 for the first insert
        final var retrieved = providerRepository.findById(new ExternalProviderId(1L));

        assertThat(retrieved).isPresent();
        final var p = retrieved.orElseThrow();
        assertThat(p.name().value()).isEqualTo(randomName);
        assertThat(p.active()).isTrue();
    }

    @Test
    @DisplayName("given existing provider, when updating name, then changes are persisted")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-provider.sql"})
    void givenExistingProvider_whenUpdatingName_thenChangesArePersisted() {
        // given
        final var providerId = new ExternalProviderId(2L);

        final var retrieved = providerRepository.findById(providerId).orElseThrow();

        final var randomName = TrainingTestFactory.randomTrainingNameString();
        final var randomEmail = "contact@example.com";

        final var updatedProvider = retrieved.update(new ExternalProviderName(randomName), new ExternalProviderContact(new Email(randomEmail), new Phone("7654321")));

        // when
        providerRepository.save(updatedProvider);

        // then
        final var updated = providerRepository.findById(providerId);
        assertThat(updated).isPresent();
        final var p = updated.orElseThrow();
        assertThat(p.name().value()).isEqualTo(randomName);
    }

    @Test
    @DisplayName("given active provider, when deactivating, then active flag is updated")
    @Sql(scripts = {"/sql/cleanup-all.sql", "/sql/insert-provider.sql"})
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
        final var p = updated.orElseThrow();
        assertThat(p.active()).isFalse();
    }
}
