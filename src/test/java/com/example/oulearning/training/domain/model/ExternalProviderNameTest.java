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

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ExternalProviderNameTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid provider name, when creating ExternalProviderName, then create successfully")
        void givenValidProviderName_whenCreatingExternalProviderName_thenCreateSuccessfully() {
            // given
            final var nameString = TrainingTestFactory.randomTypeNameString();

            // when
            final var name = ExternalProviderName.of(nameString);

            // then
            assertThat(name.value()).isEqualTo(nameString);
            assertThat(name).hasToString(nameString);
        }

        @Test
        @DisplayName("given null provider name, when creating ExternalProviderName, then throw InvalidTrainingOperationException")
        void givenNullProviderName_whenCreatingExternalProviderName_thenThrowInvalidTrainingOperationException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> ExternalProviderName.of(null))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("External provider name cannot be null");
        }

        @Test
        @DisplayName("given blank provider name, when creating ExternalProviderName, then throw InvalidTrainingOperationException")
        void givenBlankProviderName_whenCreatingExternalProviderName_thenThrowInvalidTrainingOperationException() {
            // given
            final var blank = " ".repeat(Instancio.gen().ints().range(1, 5).get());

            // when

            // then
            assertThatThrownBy(() -> ExternalProviderName.of(blank))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("External provider name cannot be blank");
        }

        @Test
        @DisplayName("given provider name exceeding max length, when creating ExternalProviderName, then throw InvalidTrainingOperationException")
        void givenProviderNameExceedingMaxLength_whenCreatingExternalProviderName_thenThrowInvalidTrainingOperationException() {
            // given
            final var longName = "A".repeat(TrainingConstants.MAX_NAME_LENGTH + 1);

            // when

            // then
            assertThatThrownBy(() -> ExternalProviderName.of(longName))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("External provider name length must be between");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical provider names, when comparing, then they are equal")
        void givenIdenticalProviderNames_whenComparing_thenTheyAreEqual() {
            // given
            final var nameString = TrainingTestFactory.randomTypeNameString();
            final var n1 = ExternalProviderName.of(nameString);
            final var n2 = ExternalProviderName.of(nameString);

            // when

            // then
            assertThat(n1).isEqualTo(n2).hasSameHashCodeAs(n2);
        }
    }
}
