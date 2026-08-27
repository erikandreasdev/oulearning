package com.example.oulearning.training.domain.model;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ExternalProviderIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid id, when creating ExternalProviderId, then create successfully")
        void givenValidId_whenCreatingExternalProviderId_thenCreateSuccessfully() {
            // given
            final var value = TrainingTestFactory.randomId();

            // when
            final var id = ExternalProviderId.of(value);

            // then
            assertThat(id.value()).isEqualTo(value);
            assertThat(id).hasToString(String.valueOf(value));
        }

        @Test
        @DisplayName("given valid id string, when parsing ExternalProviderId, then parse successfully")
        void givenValidIdString_whenParsingExternalProviderId_thenParseSuccessfully() {
            // given
            final var value = TrainingTestFactory.randomId();

            // when
            final var id = ExternalProviderId.fromString(" %d ".formatted(value));

            // then
            assertThat(id.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("given non-positive id, when creating ExternalProviderId, then throw exception")
        void givenNonPositiveId_whenCreatingExternalProviderId_thenThrowException() {
            // given
            final var nonPositiveValue = Instancio.gen().longs().range(Long.MIN_VALUE, 0L).get();

            // when

            // then
            assertThatThrownBy(() -> new ExternalProviderId(nonPositiveValue))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("must be strictly positive");
        }

        @Test
        @DisplayName("given invalid id string, when parsing ExternalProviderId, then throw exception")
        void givenInvalidIdString_whenParsingExternalProviderId_thenThrowException() {
            // given
            final var invalidId = Instancio.create(String.class);

            // when

            // then
            assertThatThrownBy(() -> ExternalProviderId.fromString(invalidId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid ExternalProviderId format");

            assertThatThrownBy(() -> ExternalProviderId.fromString(""))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot be null or blank");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical ids, when comparing ExternalProviderIds, then they are equal")
        void givenIdenticalIds_whenComparingExternalProviderIds_thenTheyAreEqual() {
            // given
            final var value = TrainingTestFactory.randomId();
            final var id1 = ExternalProviderId.of(value);
            final var id2 = ExternalProviderId.of(value);

            // when

            // then
            assertThat(id1).isEqualTo(id2).hasSameHashCodeAs(id2);
        }

        @Test
        @DisplayName("given different ids, when comparing ExternalProviderIds, then they are not equal")
        void givenDifferentIds_whenComparingExternalProviderIds_thenTheyAreNotEqual() {
            // given
            final var id1 = TrainingTestFactory.randomExternalProviderId();
            final var id2 = ExternalProviderId.of(id1.value() + 1L);

            // when

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
