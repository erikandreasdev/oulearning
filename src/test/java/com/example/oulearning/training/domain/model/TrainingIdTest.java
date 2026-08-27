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

class TrainingIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid id, when creating TrainingId, then create successfully")
        void givenValidId_whenCreatingTrainingId_thenCreateSuccessfully() {
            // given
            final var value = TrainingTestFactory.randomId();

            // when
            final var id = TrainingId.of(value);

            // then
            assertThat(id.value()).isEqualTo(value);
            assertThat(id).hasToString(String.valueOf(value));
        }

        @Test
        @DisplayName("given valid id string, when parsing TrainingId, then parse successfully")
        void givenValidIdString_whenParsingTrainingId_thenParseSuccessfully() {
            // given
            final var value = TrainingTestFactory.randomId();

            // when
            final var id = TrainingId.fromString(" %d ".formatted(value));

            // then
            assertThat(id.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("given non-positive id, when creating TrainingId, then throw exception")
        void givenNonPositiveId_whenCreatingTrainingId_thenThrowException() {
            // given
            final var nonPositiveValue = Instancio.gen().longs().range(Long.MIN_VALUE, 0L).get();

            // when

            // then
            assertThatThrownBy(() -> new TrainingId(nonPositiveValue))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("must be strictly positive");
        }

        @Test
        @DisplayName("given invalid id string, when parsing TrainingId, then throw exception")
        void givenInvalidIdString_whenParsingTrainingId_thenThrowException() {
            // given
            final var invalidId = Instancio.create(String.class);

            // when

            // then
            assertThatThrownBy(() -> TrainingId.fromString(invalidId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid Training id format");

            assertThatThrownBy(() -> TrainingId.fromString(""))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot be null or blank");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical ids, when comparing TrainingIds, then they are equal")
        void givenIdenticalIds_whenComparingTrainingIds_thenTheyAreEqual() {
            // given
            final var value = TrainingTestFactory.randomId();
            final var id1 = TrainingId.of(value);
            final var id2 = TrainingId.of(value);

            // when

            // then
            assertThat(id1).isEqualTo(id2).hasSameHashCodeAs(id2);
        }

        @Test
        @DisplayName("given different ids, when comparing TrainingIds, then they are not equal")
        void givenDifferentIds_whenComparingTrainingIds_thenTheyAreNotEqual() {
            // given
            final var id1 = TrainingTestFactory.randomTrainingId();
            final var id2 = TrainingId.of(id1.value() + 1L);

            // when

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
