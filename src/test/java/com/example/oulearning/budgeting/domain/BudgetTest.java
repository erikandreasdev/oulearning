package com.example.oulearning.budgeting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.budgeting.domain.event.BudgetAllocated;
import com.example.oulearning.budgeting.domain.event.BudgetCreated;
import com.example.oulearning.budgeting.domain.event.BudgetReserved;
import com.example.oulearning.budgeting.domain.event.ReservationReleased;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BudgetTest {

    private final Id id = Id.of(UUID.randomUUID());
    private final com.example.oulearning.organization.domain.hierarchy.Id ouId =
            com.example.oulearning.organization.domain.hierarchy.Id.of(UUID.randomUUID());
    private final FiscalYear fiscalYear = FiscalYear.of(2026);
    private final Money total = Money.of(10000.0, "EUR");
    private final Instant now = Instant.parse("2026-08-19T10:00:00Z");

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create budget and register BudgetCreated event")
        void should_createBudget_and_registerEvent() {
            Budget budget = Budget.create(id, ouId, fiscalYear, total, now);

            assertThat(budget.id()).isEqualTo(id);
            assertThat(budget.ouId()).isEqualTo(ouId);
            assertThat(budget.fiscalYear()).isEqualTo(fiscalYear);
            assertThat(budget.total()).isEqualTo(total);
            assertThat(budget.reserved()).isEqualTo(Money.zero("EUR"));
            assertThat(budget.available()).isEqualTo(total);

            List<Object> events = budget.pullDomainEvents();
            assertThat(events).containsExactly(new BudgetCreated(id, ouId, fiscalYear, total, now));
            assertThat(budget.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should reconstitute budget without registering events")
        void should_reconstituteBudget_withoutEvents() {
            Money reserved = Money.of(2000.0, "EUR");
            Money available = Money.of(8000.0, "EUR");

            Budget budget = Budget.reconstitute(id, ouId, fiscalYear, total, reserved, available);

            assertThat(budget.id()).isEqualTo(id);
            assertThat(budget.total()).isEqualTo(total);
            assertThat(budget.reserved()).isEqualTo(reserved);
            assertThat(budget.available()).isEqualTo(available);
            assertThat(budget.pullDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("should throw InvalidBudgetOperationException when invariant total = reserved + available is violated")
        void should_throwException_when_invariantViolated() {
            Money reserved = Money.of(2000.0, "EUR");
            Money available = Money.of(7000.0, "EUR"); // 2000 + 7000 != 10000

            assertThatThrownBy(() -> Budget.reconstitute(id, ouId, fiscalYear, total, reserved, available))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("Budget invariant violated");
        }
    }

    @Nested
    @DisplayName("Budget Operations")
    class BudgetOperations {

        @Test
        @DisplayName("should reserve budget and register BudgetReserved event")
        void should_reserveBudget_and_registerEvent() {
            Budget budget = Budget.create(id, ouId, fiscalYear, total, now);
            Money reservationAmount = Money.of(3000.0, "EUR");
            Instant reserveTime = now.plusSeconds(3600);

            budget.reserve(reservationAmount, reserveTime);

            assertThat(budget.reserved()).isEqualTo(Money.of(3000.0, "EUR"));
            assertThat(budget.available()).isEqualTo(Money.of(7000.0, "EUR"));

            List<Object> events = budget.pullDomainEvents();
            assertThat(events).hasSize(2); // BudgetCreated + BudgetReserved
            assertThat(events.get(1))
                    .isEqualTo(new BudgetReserved(
                            id, reservationAmount, Money.of(3000.0, "EUR"), Money.of(7000.0, "EUR"), reserveTime));
        }

        @Test
        @DisplayName("should throw InsufficientBudgetException when reservation exceeds available")
        void should_throwException_when_reservationExceedsAvailable() {
            Budget budget = Budget.create(id, ouId, fiscalYear, total, now);
            Money excessiveAmount = Money.of(15000.0, "EUR");

            assertThatThrownBy(() -> budget.reserve(excessiveAmount, now))
                    .isInstanceOf(InsufficientBudgetException.class)
                    .hasMessageContaining("Cannot reserve");
        }

        @Test
        @DisplayName("should release reservation and register ReservationReleased event")
        void should_releaseReservation_and_registerEvent() {
            Budget budget = Budget.create(id, ouId, fiscalYear, total, now);
            budget.reserve(Money.of(4000.0, "EUR"), now);
            budget.pullDomainEvents(); // Clear events

            Money releaseAmount = Money.of(1500.0, "EUR");
            Instant releaseTime = now.plusSeconds(7200);
            budget.releaseReservation(releaseAmount, releaseTime);

            assertThat(budget.reserved()).isEqualTo(Money.of(2500.0, "EUR"));
            assertThat(budget.available()).isEqualTo(Money.of(7500.0, "EUR"));

            assertThat(budget.pullDomainEvents())
                    .containsExactly(new ReservationReleased(
                            id, releaseAmount, Money.of(2500.0, "EUR"), Money.of(7500.0, "EUR"), releaseTime));
        }

        @Test
        @DisplayName("should throw InvalidBudgetOperationException when release exceeds reserved")
        void should_throwException_when_releaseExceedsReserved() {
            Budget budget = Budget.create(id, ouId, fiscalYear, total, now);
            budget.reserve(Money.of(2000.0, "EUR"), now);

            assertThatThrownBy(() -> budget.releaseReservation(Money.of(3000.0, "EUR"), now))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("Cannot release");
        }

        @Test
        @DisplayName("should allocate additional funds and register BudgetAllocated event")
        void should_allocateFunds_and_registerEvent() {
            Budget budget = Budget.create(id, ouId, fiscalYear, total, now);
            budget.pullDomainEvents();

            Money extra = Money.of(5000.0, "EUR");
            Instant allocTime = now.plusSeconds(3600);
            budget.allocate(extra, allocTime);

            assertThat(budget.total()).isEqualTo(Money.of(15000.0, "EUR"));
            assertThat(budget.available()).isEqualTo(Money.of(15000.0, "EUR"));

            assertThat(budget.pullDomainEvents())
                    .containsExactly(new BudgetAllocated(
                            id, extra, Money.of(15000.0, "EUR"), Money.of(15000.0, "EUR"), allocTime));
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("should be equal when ids match")
        void should_beEqual_when_idsMatch() {
            Budget b1 = Budget.create(id, ouId, fiscalYear, total, now);
            Budget b2 = Budget.create(
                    id,
                    com.example.oulearning.organization.domain.hierarchy.Id.of(UUID.randomUUID()),
                    FiscalYear.of(2027),
                    Money.of(500.0, "EUR"),
                    now);

            assertThat(b1).isEqualTo(b2);
            assertThat(b1.hashCode()).isEqualTo(b2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when ids differ")
        void should_notBeEqual_when_idsDiffer() {
            Budget b1 = Budget.create(id, ouId, fiscalYear, total, now);
            Budget b2 = Budget.create(Id.of(UUID.randomUUID()), ouId, fiscalYear, total, now);

            assertThat(b1).isNotEqualTo(b2);
        }
    }
}
