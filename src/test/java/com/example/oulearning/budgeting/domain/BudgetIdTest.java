package com.example.oulearning.budgeting.domain;

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
        @DisplayName("given valid UUID, when creating BudgetId, then create successfully")
        void givenValidUuid_whenCreatingBudgetId_thenCreateSuccessfully() {
            // given
            final var uuid = BudgetingTestFactory.randomUuid();

            // when
            final var id = BudgetId.of(uuid);

            // then
            assertThat(id.value()).isEqualTo(uuid);
            assertThat(id.toString()).isEqualTo(uuid.toString());
        }

        @Test
        @DisplayName("given valid UUID string, when parsing BudgetId, then parse successfully")
        void givenValidUuidString_whenParsingBudgetId_thenParseSuccessfully() {
            // given
            final var uuid = BudgetingTestFactory.randomUuid();

            // when
            final var id = BudgetId.fromString(" %s ".formatted(uuid));

            // then
            assertThat(id.value()).isEqualTo(uuid);
        }

        @Test
        @DisplayName("given null UUID, when creating BudgetId, then throw exception")
        void givenNullUuid_whenCreatingBudgetId_thenThrowException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> new BudgetId(null))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot be null");
        }

        @Test
        @DisplayName("given invalid UUID string, when parsing BudgetId, then throw exception")
        void givenInvalidUuidString_whenParsingBudgetId_thenThrowException() {
            // given
            final var invalidUuid = Instancio.create(String.class);

            // when

            // then
            assertThatThrownBy(() -> BudgetId.fromString(invalidUuid))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Invalid UUID format");

            assertThatThrownBy(() -> BudgetId.fromString(""))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("cannot be null or blank");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical UUIDs, when comparing BudgetIds, then they are equal")
        void givenIdenticalUuids_whenComparingBudgetIds_thenTheyAreEqual() {
            // given
            final var uuid = BudgetingTestFactory.randomUuid();
            final var id1 = BudgetId.of(uuid);
            final var id2 = BudgetId.of(uuid);

            // when

            // then
            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("given different UUIDs, when comparing BudgetIds, then they are not equal")
        void givenDifferentUuids_whenComparingBudgetIds_thenTheyAreNotEqual() {
            // given
            final var id1 = BudgetingTestFactory.randomBudgetId();
            final var id2 = BudgetingTestFactory.randomBudgetId();

            // when

            // then
            assertThat(id1).isNotEqualTo(id2);
        }
    }
}
