package com.example.oulearning.budgeting.domain.model;

import com.example.oulearning.budgeting.application.port.in.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
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

import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BudgetIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid id, when creating BudgetId, then create successfully")
        void givenValidId_whenCreatingBudgetId_thenCreateSuccessfully() {
            // given
            final var value = BudgetingTestFactory.randomId();

            // when
            final var id = BudgetId.of(value);

            // then
            assertThat(id.value()).isEqualTo(value);
            assertThat(id).hasToString(String.valueOf(value));
        }

        @Test
        @DisplayName("given valid id string, when parsing BudgetId, then parse successfully")
        void givenValidIdString_whenParsingBudgetId_thenParseSuccessfully() {
            // given
            final var value = BudgetingTestFactory.randomId();

            // when
            final var id = BudgetId.fromString(" %d ".formatted(value));

            // then
            assertThat(id.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("given non-positive id, when creating BudgetId, then throw exception")
        void givenNonPositiveId_whenCreatingBudgetId_thenThrowException() {
            // given
            final var nonPositiveValue = Instancio.gen().longs().range(Long.MIN_VALUE, 0L).get();

            // when

            // then
            assertThatThrownBy(() -> new BudgetId(nonPositiveValue))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("must be strictly positive");
        }

        @Test
        @DisplayName("given invalid id string, when parsing BudgetId, then throw exception")
        void givenInvalidIdString_whenParsingBudgetId_thenThrowException() {
            // given
            final var invalidId = Instancio.create(String.class);

            // when

            // then
            assertThatThrownBy(() -> BudgetId.fromString(invalidId))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid Budget id format");

            assertThatThrownBy(() -> BudgetId.fromString(""))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot be null or blank");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical ids, when comparing BudgetIds, then they are equal")
        void givenIdenticalIds_whenComparingBudgetIds_thenTheyAreEqual() {
            // given
            final var value = BudgetingTestFactory.randomId();
            final var id1 = BudgetId.of(value);
            final var id2 = BudgetId.of(value);

            // when

            // then
            assertThat(id1).isEqualTo(id2).hasSameHashCodeAs(id2);
        }

        @Test
        @DisplayName("given different ids, when comparing BudgetIds, then they are not equal")
        void givenDifferentIds_whenComparingBudgetIds_thenTheyAreNotEqual() {
            // given
            final var id1 = BudgetingTestFactory.randomBudgetId();
            final var id2 = BudgetId.of(id1.value() + 1L);

            // when

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
