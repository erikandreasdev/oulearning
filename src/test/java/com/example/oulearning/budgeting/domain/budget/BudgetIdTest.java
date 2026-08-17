package com.example.oulearning.budgeting.domain.budget;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.budgeting.domain.budget.exception.InvalidBudgetException;
import java.util.UUID;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(InstancioExtension.class)
class BudgetIdTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("should create BudgetId from UUID")
        void should_createBudgetId_from_uuid() {
            final var uuid = UUID.randomUUID();
            final var budgetId = BudgetId.of(uuid);

            assertThat(budgetId.value()).isEqualTo(uuid);
            assertThat(budgetId.toString()).isEqualTo(uuid.toString());
        }

        @Test
        @DisplayName("should create BudgetId from valid string")
        void should_createBudgetId_from_validString() {
            final var uuid = UUID.randomUUID();
            final var budgetId = BudgetId.of(uuid.toString());

            assertThat(budgetId.value()).isEqualTo(uuid);
            assertThat(budgetId.toString()).isEqualTo(uuid.toString());
        }

        @Test
        @DisplayName("should throw InvalidBudgetException when UUID is null")
        void should_throwException_when_uuidIsNull() {
            assertThatThrownBy(() -> new BudgetId(null))
                    .isInstanceOf(InvalidBudgetException.class)
                    .hasMessageContaining("cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "\t"})
        @DisplayName("should throw InvalidBudgetException when string is blank")
        void should_throwException_when_stringIsBlank(String blank) {
            assertThatThrownBy(() -> BudgetId.of(blank))
                    .isInstanceOf(InvalidBudgetException.class)
                    .hasMessageContaining("cannot be null or blank");
        }

        @Test
        @DisplayName("should throw InvalidBudgetException when string is not a valid UUID")
        void should_throwException_when_stringIsNotValidUuid() {
            assertThatThrownBy(() -> BudgetId.of("invalid-uuid"))
                    .isInstanceOf(InvalidBudgetException.class)
                    .hasMessageContaining("Invalid UUID format");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when UUIDs match")
        void should_beEqual_when_uuidsMatch() {
            final var uuid = UUID.randomUUID();
            final var id1 = BudgetId.of(uuid);
            final var id2 = BudgetId.of(uuid);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when UUIDs differ")
        void should_notBeEqual_when_uuidsDiffer() {
            final var id1 = BudgetId.of(UUID.randomUUID());
            final var id2 = BudgetId.of(UUID.randomUUID());

            assertThat(id1).isNotEqualTo(id2);
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var id = BudgetId.of(UUID.randomUUID());
            assertThat(id.getClass().isRecord()).isTrue();
        }
    }
}
