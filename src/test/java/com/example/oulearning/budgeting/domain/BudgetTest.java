package com.example.oulearning.budgeting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.budgeting.domain.exception.InvalidBudgetOperationException;
import com.example.oulearning.organization.domain.hierarchy.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BudgetTest {

    private final BudgetId id = BudgetingTestFactory.randomBudgetId();
    private final OrganizationalUnitId organizationalUnitId = HierarchyTestFactory.randomOrganizationalUnitId();
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
            // given

            // when
            final var budget = Budget.of(id, organizationalUnitId, fiscalYear, total, reserved, available);

            // then
            assertThat(budget.id()).isEqualTo(id);
            assertThat(budget.organizationalUnitId()).isEqualTo(organizationalUnitId);
            assertThat(budget.fiscalYear()).isEqualTo(fiscalYear);
            assertThat(budget.total()).isEqualTo(total);
            assertThat(budget.reserved()).isEqualTo(reserved);
            assertThat(budget.available()).isEqualTo(available);
        }

        @Test
        @DisplayName("given null parameters, when creating Budget, then throw InvalidBudgetOperationException")
        void givenNullParameters_whenCreatingBudget_thenThrowInvalidBudgetOperationException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> Budget.of(null, organizationalUnitId, fiscalYear, total, reserved, available))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Budget.of(id, null, fiscalYear, total, reserved, available))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Budget.of(id, organizationalUnitId, null, total, reserved, available))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Budget.of(id, organizationalUnitId, fiscalYear, null, reserved, available))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Budget.of(id, organizationalUnitId, fiscalYear, total, null, available))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("cannot be null");
            assertThatThrownBy(() -> Budget.of(id, organizationalUnitId, fiscalYear, total, reserved, null))
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
            // given
            final var b1 = Budget.of(id, organizationalUnitId, fiscalYear, total, reserved, available);
            final var b2 = Budget.of(
                    id,
                    HierarchyTestFactory.randomOrganizationalUnitId(),
                    BudgetingTestFactory.randomFiscalYear(),
                    BudgetingTestFactory.randomMoney(),
                    Money.zero(),
                    BudgetingTestFactory.randomMoney());

            // when

            // then
            assertThat(b1).isEqualTo(b2).hasSameHashCodeAs(b2);
        }

        @Test
        @DisplayName("given budgets with different ids, when comparing, then they are not equal")
        void givenBudgetsWithDifferentIds_whenComparing_thenTheyAreNotEqual() {
            // given
            final var b1 = Budget.of(id, organizationalUnitId, fiscalYear, total, reserved, available);
            final var b2 = Budget.of(
                    BudgetingTestFactory.randomBudgetId(),
                    organizationalUnitId,
                    fiscalYear,
                    total,
                    reserved,
                    available);

            // when

            // then
            assertThat(b1).isNotEqualTo(b2);
        }

        @Test
        @DisplayName("given same budget instance, when comparing, then they are equal")
        void givenSameBudgetInstance_whenComparing_thenTheyAreEqual() {
            // given
            final var b = Budget.of(id, organizationalUnitId, fiscalYear, total, reserved, available);

            // when

            // then
            assertThat(b).isEqualTo(b);
        }

        @Test
        @DisplayName("given null or different object type, when comparing, then they are not equal")
        void givenNullOrDifferentType_whenComparing_thenTheyAreNotEqual() {
            // given
            final var b = Budget.of(id, organizationalUnitId, fiscalYear, total, reserved, available);

            // when

            // then
            assertThat(b).isNotEqualTo(null).isNotEqualTo(new Object());
        }
    }
}
