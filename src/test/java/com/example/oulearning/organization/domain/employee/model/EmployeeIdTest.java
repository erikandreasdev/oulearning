package com.example.oulearning.organization.domain.employee.model;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.port.in.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.application.employee.port.in.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.port.in.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.port.in.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmployeeException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class EmployeeIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid id, when creating EmployeeId, then id is created successfully")
        void givenValidId_whenCreatingEmployeeId_thenIdIsCreatedSuccessfully() {
            // given
            final var value = EmployeeTestFactory.randomId();

            // when
            final var id = EmployeeId.of(value);

            // then
            assertThat(id.value()).isEqualTo(value);
            assertThat(id).hasToString(String.valueOf(value));
        }

        @Test
        @DisplayName("given valid id string, when parsing EmployeeId, then id is parsed successfully")
        void givenValidIdString_whenParsingEmployeeId_thenIdIsParsedSuccessfully() {
            // given
            final var value = EmployeeTestFactory.randomId();
            final var idString = " %d ".formatted(value);

            // when
            final var id = EmployeeId.fromString(idString);

            // then
            assertThat(id.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("given non-positive id, when creating EmployeeId, then throw InvalidEmployeeException")
        void givenNonPositiveId_whenCreatingEmployeeId_thenThrowInvalidEmployeeException() {
            // given
            final var nonPositiveValue = Instancio.gen().longs().range(Long.MIN_VALUE, 0L).get();

            // when

            // then
            assertThatThrownBy(() -> new EmployeeId(nonPositiveValue))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("must be strictly positive");
        }

        @Test
        @DisplayName("given blank id string, when parsing EmployeeId, then throw InvalidEmployeeException")
        void givenBlankIdString_whenParsingEmployeeId_thenThrowInvalidEmployeeException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> EmployeeId.fromString("   "))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("cannot be null or blank");
        }

        @Test
        @DisplayName("given invalid id string, when parsing EmployeeId, then throw InvalidEmployeeException")
        void givenInvalidIdString_whenParsingEmployeeId_thenThrowInvalidEmployeeException() {
            // given
            final var invalidId = Instancio.create(String.class);

            // when

            // then
            assertThatThrownBy(() -> EmployeeId.fromString(invalidId))
                    .isInstanceOf(InvalidEmployeeException.class)
                    .hasMessageContaining("Invalid Employee id format");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical ids, when comparing EmployeeIds, then they are equal")
        void givenIdenticalIds_whenComparingEmployeeIds_thenTheyAreEqual() {
            // given
            final var value = EmployeeTestFactory.randomId();
            final var id1 = EmployeeId.of(value);
            final var id2 = EmployeeId.of(value);

            // when

            // then
            assertThat(id1).isEqualTo(id2).hasSameHashCodeAs(id2);
        }

        @Test
        @DisplayName("given different ids, when comparing EmployeeIds, then they are not equal")
        void givenDifferentIds_whenComparingEmployeeIds_thenTheyAreNotEqual() {
            // given
            final var id1 = EmployeeTestFactory.randomEmployeeId();
            final var id2 = EmployeeId.of(id1.value() + 1L);

            // when

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
