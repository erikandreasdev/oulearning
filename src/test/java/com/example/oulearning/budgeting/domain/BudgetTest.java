package com.example.oulearning.budgeting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.hierarchy.OuId;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BudgetTest {

    private final BudgetId id = BudgetId.of(UUID.randomUUID());
    private final OuId ouId = OuId.of(UUID.randomUUID());
    private final FiscalYear fiscalYear = FiscalYear.of(2026);
    private final Money total = Money.of(10000.0, "EUR");
    private final Money reserved = Money.of(2000.0, "EUR");
    private final Money available = Money.of(8000.0, "EUR");

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("should create budget with all fields")
        void should_createBudget_withAllFields() {
            Budget budget = Budget.of(id, ouId, fiscalYear, total, reserved, available);

            assertThat(budget.id()).isEqualTo(id);
            assertThat(budget.ouId()).isEqualTo(ouId);
            assertThat(budget.fiscalYear()).isEqualTo(fiscalYear);
            assertThat(budget.total()).isEqualTo(total);
            assertThat(budget.reserved()).isEqualTo(reserved);
            assertThat(budget.available()).isEqualTo(available);
        }

        @Test
        @DisplayName("should throw NullPointerException when required parameters are null")
        void should_throwException_when_requiredNull() {
            assertThatThrownBy(() -> new Budget(null, ouId, fiscalYear, total, reserved, available))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new Budget(id, null, fiscalYear, total, reserved, available))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new Budget(id, ouId, null, total, reserved, available))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new Budget(id, ouId, fiscalYear, null, reserved, available))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new Budget(id, ouId, fiscalYear, total, null, available))
                    .isInstanceOf(NullPointerException.class);
            assertThatThrownBy(() -> new Budget(id, ouId, fiscalYear, total, reserved, null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("should be equal when ids match")
        void should_beEqual_when_idsMatch() {
            Budget b1 = Budget.of(id, ouId, fiscalYear, total, reserved, available);
            Budget b2 = Budget.of(
                    id,
                    OuId.of(UUID.randomUUID()),
                    FiscalYear.of(2027),
                    Money.of(500.0, "EUR"),
                    Money.zero("EUR"),
                    Money.of(500.0, "EUR"));

            assertThat(b1).isEqualTo(b2);
            assertThat(b1.hashCode()).isEqualTo(b2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when ids differ")
        void should_notBeEqual_when_idsDiffer() {
            Budget b1 = Budget.of(id, ouId, fiscalYear, total, reserved, available);
            Budget b2 = Budget.of(BudgetId.of(UUID.randomUUID()), ouId, fiscalYear, total, reserved, available);

            assertThat(b1).isNotEqualTo(b2);
        }
    }
}
