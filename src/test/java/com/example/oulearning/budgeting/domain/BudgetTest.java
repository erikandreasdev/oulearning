package com.example.oulearning.budgeting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.shared.domain.Money;
import com.example.oulearning.shared.domain.OuId;
import java.util.UUID;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(InstancioExtension.class)
class BudgetTest {

    private BudgetId randomBudgetId() {
        return BudgetId.of(UUID.randomUUID());
    }

    private OuId randomOuId() {
        return OuId.of(UUID.randomUUID());
    }

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create zero budget by default")
        void should_createZeroBudget() {
            final var budgetId = randomBudgetId();
            final var ouId = randomOuId();

            final var budget = Budget.zero(budgetId, ouId);

            assertThat(budget.allocated()).isEqualTo(Money.zero());
            assertThat(budget.reserved()).isEqualTo(Money.zero());
            assertThat(budget.spent()).isEqualTo(Money.zero());
            assertThat(budget.available()).isEqualTo(Money.zero());
        }

        @Test
        @DisplayName("should create budget with allocated funds")
        void should_createBudget_withAllocatedFunds() {
            final var budgetId = randomBudgetId();
            final var ouId = randomOuId();
            final var allocated = Money.euros(10000.00);

            final var budget = Budget.of(budgetId, ouId, allocated);

            assertThat(budget.allocated()).isEqualTo(allocated);
            assertThat(budget.reserved()).isEqualTo(Money.zero());
            assertThat(budget.spent()).isEqualTo(Money.zero());
            assertThat(budget.available()).isEqualTo(allocated);
        }

        @Test
        @DisplayName("should throw InvalidBudgetException when reserved + spent exceeds allocated")
        void should_throwException_when_committedExceedsAllocated() {
            final var budgetId = randomBudgetId();
            final var ouId = randomOuId();
            final var allocated = Money.euros(5000.00);
            final var reserved = Money.euros(3000.00);
            final var spent = Money.euros(3000.00); // 6000 > 5000

            assertThatThrownBy(() -> new Budget(budgetId, ouId, allocated, reserved, spent))
                    .isInstanceOf(InvalidBudgetException.class)
                    .hasMessageContaining("cannot exceed allocated budget");
        }

        @Test
        @DisplayName("should throw InvalidBudgetException when fields are null")
        void should_throwException_when_fieldsAreNull() {
            final var budgetId = randomBudgetId();
            final var ouId = randomOuId();
            final var zero = Money.zero();

            assertThatThrownBy(() -> new Budget(null, ouId, zero, zero, zero))
                    .isInstanceOf(InvalidBudgetException.class);
            assertThatThrownBy(() -> new Budget(budgetId, null, zero, zero, zero))
                    .isInstanceOf(InvalidBudgetException.class);
            assertThatThrownBy(() -> new Budget(budgetId, ouId, null, zero, zero))
                    .isInstanceOf(InvalidBudgetException.class);
        }
    }

    @Nested
    @DisplayName("Fund Operations (Reserve, Release, Consume, SpendDirect)")
    class FundOperations {

        @Test
        @DisplayName("should reserve available funds correctly")
        void should_reserveFunds_correctly() {
            // given
            final var budget = Budget.of(randomBudgetId(), randomOuId(), Money.euros(10000.00));

            // when
            final var updated = budget.reserve(Money.euros(3000.00));

            // then
            assertThat(updated.allocated()).isEqualTo(Money.euros(10000.00));
            assertThat(updated.reserved()).isEqualTo(Money.euros(3000.00));
            assertThat(updated.spent()).isEqualTo(Money.zero());
            assertThat(updated.available()).isEqualTo(Money.euros(7000.00));
        }

        @Test
        @DisplayName("should throw InsufficientBudgetException when reserving more than available")
        void should_throwException_when_reservingMoreThanAvailable() {
            final var budget = Budget.of(randomBudgetId(), randomOuId(), Money.euros(5000.00));

            assertThatThrownBy(() -> budget.reserve(Money.euros(6000.00)))
                    .isInstanceOf(InsufficientBudgetException.class)
                    .hasMessageContaining("Cannot reserve EUR 6000.00: available balance is only EUR 5000.00");
        }

        @Test
        @DisplayName("should release reserved funds back to available balance")
        void should_releaseReservedFunds() {
            // given
            final var initial = Budget.of(randomBudgetId(), randomOuId(), Money.euros(10000.00))
                    .reserve(Money.euros(4000.00));

            // when
            final var released = initial.releaseReservation(Money.euros(1500.00));

            // then
            assertThat(released.reserved()).isEqualTo(Money.euros(2500.00));
            assertThat(released.available()).isEqualTo(Money.euros(7500.00));
        }

        @Test
        @DisplayName("should consume reserved funds when finalizing an expense")
        void should_consumeReservedFunds() {
            // given
            final var initial = Budget.of(randomBudgetId(), randomOuId(), Money.euros(10000.00))
                    .reserve(Money.euros(4000.00));

            // when
            final var consumed = initial.consumeReserved(Money.euros(3000.00));

            // then
            assertThat(consumed.reserved()).isEqualTo(Money.euros(1000.00));
            assertThat(consumed.spent()).isEqualTo(Money.euros(3000.00));
            assertThat(consumed.available()).isEqualTo(Money.euros(6000.00));
        }

        @Test
        @DisplayName("should spend funds directly from available balance")
        void should_spendDirect_fromAvailableFunds() {
            // given
            final var budget = Budget.of(randomBudgetId(), randomOuId(), Money.euros(10000.00));

            // when
            final var spent = budget.spendDirect(Money.euros(2500.00));

            // then
            assertThat(spent.spent()).isEqualTo(Money.euros(2500.00));
            assertThat(spent.reserved()).isEqualTo(Money.zero());
            assertThat(spent.available()).isEqualTo(Money.euros(7500.00));
        }

        @Test
        @DisplayName("should throw InsufficientBudgetException when direct spend exceeds available")
        void should_throwException_when_directSpendExceedsAvailable() {
            final var budget = Budget.of(randomBudgetId(), randomOuId(), Money.euros(2000.00));

            assertThatThrownBy(() -> budget.spendDirect(Money.euros(3000.00)))
                    .isInstanceOf(InsufficientBudgetException.class)
                    .hasMessageContaining("Cannot spend EUR 3000.00 directly");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics & Immutability")
    class ValueObjectSemanticsAndImmutability {

        @Test
        @DisplayName("should be equal when all fields match")
        void should_beEqual_when_allFieldsMatch() {
            final var budgetId = randomBudgetId();
            final var ouId = randomOuId();
            final var allocated = Money.euros(5000.00);

            final var b1 = Budget.of(budgetId, ouId, allocated);
            final var b2 = Budget.of(budgetId, ouId, allocated);

            assertThat(b1).isEqualTo(b2);
            assertThat(b1.hashCode()).isEqualTo(b2.hashCode());
        }

        @Test
        @DisplayName("should maintain record immutability")
        void should_maintainImmutability() {
            final var budget = Budget.of(randomBudgetId(), randomOuId(), Money.euros(5000.00));
            assertThat(budget.getClass().isRecord()).isTrue();
        }
    }
}
