package com.example.oulearning.budgeting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.budgeting.domain.exception.InvalidBudgetOperationException;
import com.example.oulearning.organization.domain.hierarchy.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.OuId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BudgetTest {

    private final BudgetId id = BudgetingTestFactory.randomBudgetId();
    private final OuId ouId = HierarchyTestFactory.randomOuId();
    private final FiscalYear fiscalYear = BudgetingTestFactory.randomFiscalYear();
    private final Money total = BudgetingTestFactory.randomMoney();
    private final Money reserved = BudgetingTestFactory.randomMoney();
    private final Money available = BudgetingTestFactory.randomMoney();

    @Nested
    @DisplayName("Creation and Invariants")
    class CreationAndInvariants {

        @Test
        @DisplayName("given valid fields, when creating Budget, then budget is created successfully")
        void givenValidFields_whenCreatingBudget_thenBudgetIsCreatedSuccessfully() {



            final var budget = Budget.of(id, ouId, fiscalYear, total, reserved, available);


            assertThat(budget.id()).isEqualTo(id);
            assertThat(budget.ouId()).isEqualTo(ouId);
            assertThat(budget.fiscalYear()).isEqualTo(fiscalYear);
            assertThat(budget.total()).isEqualTo(total);
            assertThat(budget.reserved()).isEqualTo(reserved);
            assertThat(budget.available()).isEqualTo(available);
            assertThat(budget.toString())
                    .isEqualTo("Budget[id=%s, ouId=%s, fiscalYear=%s, total=%s, reserved=%s, available=%s]"
                            .formatted(id, ouId, fiscalYear, total, reserved, available));
        }

        @Test
        @DisplayName("given null parameters, when creating Budget, then throw InvalidBudgetOperationException")
        void givenNullParameters_whenCreatingBudget_thenThrowInvalidBudgetOperationException() {





            assertThatThrownBy(() -> Budget.of(null, ouId, fiscalYear, total, reserved, available))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Budget.of(id, null, fiscalYear, total, reserved, available))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Budget.of(id, ouId, null, total, reserved, available))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Budget.of(id, ouId, fiscalYear, null, reserved, available))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Budget.of(id, ouId, fiscalYear, total, null, available))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Budget.of(id, ouId, fiscalYear, total, reserved, null))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("cannot be null");
        }
    }

    @Nested
    @DisplayName("Identity and Equality")
    class IdentityAndEquality {

        @Test
        @DisplayName("given budgets with same id, when comparing, then they are equal")
        void givenBudgetsWithSameId_whenComparing_thenTheyAreEqual() {

            final var b1 = Budget.of(id, ouId, fiscalYear, total, reserved, available);
            final var b2 = Budget.of(
                    id,
                    HierarchyTestFactory.randomOuId(),
                    BudgetingTestFactory.randomFiscalYear(),
                    BudgetingTestFactory.randomMoney(),
                    Money.zero(),
                    BudgetingTestFactory.randomMoney());




            assertThat(b1).isEqualTo(b2);
            assertThat(b1.hashCode()).isEqualTo(b2.hashCode());
        }

        @Test
        @DisplayName("given budgets with different ids, when comparing, then they are not equal")
        void givenBudgetsWithDifferentIds_whenComparing_thenTheyAreNotEqual() {

            final var b1 = Budget.of(id, ouId, fiscalYear, total, reserved, available);
            final var b2 = Budget.of(
                    BudgetingTestFactory.randomBudgetId(), ouId, fiscalYear, total, reserved, available);




            assertThat(b1).isNotEqualTo(b2);
        }
    }
}
