package com.example.oulearning.training.domain.model;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.port.in.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.port.in.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.port.in.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.application.port.in.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HoursTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given positive integer from TrainingTestFactory, when creating Hours, then create successfully")
        void givenPositiveInteger_whenCreatingHours_thenCreateSuccessfully() {
            // given
            final var val = TrainingTestFactory.randomHoursValue();

            // when
            final var hours = Hours.of(val);

            // then
            assertThat(hours.value()).isEqualTo(val);
            assertThat(hours).hasToString(String.valueOf(val));
        }

        @Test
        @DisplayName("given non-positive integer, when creating Hours, then throw InvalidTrainingOperationException")
        void givenNonPositiveInteger_whenCreatingHours_thenThrowInvalidTrainingOperationException() {
            // given
            final var invalidVal = Instancio.gen()
                    .ints()
                    .max(TrainingConstants.MIN_HOURS - 1)
                    .get();

            // when

            // then
            assertThatThrownBy(() -> Hours.of(invalidVal))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("strictly positive");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical hours, when comparing Hours, then they are equal")
        void givenIdenticalHours_whenComparingHours_thenTheyAreEqual() {
            // given
            final var val = TrainingTestFactory.randomHoursValue();
            final var h1 = Hours.of(val);
            final var h2 = Hours.of(val);

            // when

            // then
            assertThat(h1).isEqualTo(h2).hasSameHashCodeAs(h2);
        }

        @Test
        @DisplayName("given different hours, when comparing Hours, then they are not equal")
        void givenDifferentHours_whenComparingHours_thenTheyAreNotEqual() {
            // given
            final var h1 = TrainingTestFactory.randomHours();
            final var h2 = Hours.of(h1.value() + 10);

            // when

            // then
            assertThat(h1).isNotEqualTo(h2);
        }
    }
}
