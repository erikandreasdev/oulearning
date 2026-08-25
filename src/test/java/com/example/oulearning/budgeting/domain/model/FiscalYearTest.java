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

import com.example.oulearning.budgeting.domain.exception.InvalidBudgetOperationException;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FiscalYearTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given valid year within bounds, when creating FiscalYear, then create successfully")
        void givenValidYearWithinBounds_whenCreatingFiscalYear_thenCreateSuccessfully() {
            // given
            final var year = BudgetingTestFactory.randomFiscalYearValue();

            // when
            final var fiscalYear = FiscalYear.of(year);

            // then
            assertThat(fiscalYear.value()).isEqualTo(year);
            assertThat(fiscalYear).hasToString(String.valueOf(year));
        }

        @Test
        @DisplayName("given year below min bounds, when creating FiscalYear, then throw InvalidBudgetOperationException")
        void givenYearBelowMinBounds_whenCreatingFiscalYear_thenThrowInvalidBudgetOperationException() {
            // given
            final var invalidYear = Instancio.gen()
                    .ints()
                    .max(BudgetingConstants.MIN_FISCAL_YEAR - 1)
                    .get();

            // when

            // then
            assertThatThrownBy(() -> FiscalYear.of(invalidYear))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("Fiscal year must be between");
        }

        @Test
        @DisplayName("given year above max bounds, when creating FiscalYear, then throw InvalidBudgetOperationException")
        void givenYearAboveMaxBounds_whenCreatingFiscalYear_thenThrowInvalidBudgetOperationException() {
            // given
            final var invalidYear = Instancio.gen()
                    .ints()
                    .min(BudgetingConstants.MAX_FISCAL_YEAR + 1)
                    .get();

            // when

            // then
            assertThatThrownBy(() -> FiscalYear.of(invalidYear))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("Fiscal year must be between");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical years, when comparing FiscalYear, then they are equal")
        void givenIdenticalYears_whenComparingFiscalYear_thenTheyAreEqual() {
            // given
            final var y = BudgetingTestFactory.randomFiscalYearValue();
            final var y1 = FiscalYear.of(y);
            final var y2 = FiscalYear.of(y);

            // when

            // then
            assertThat(y1).isEqualTo(y2).hasSameHashCodeAs(y2);
        }

        @Test
        @DisplayName("given different years, when comparing FiscalYear, then they are not equal")
        void givenDifferentYears_whenComparingFiscalYear_thenTheyAreNotEqual() {
            // given
            final var y = BudgetingTestFactory.randomFiscalYearValue();
            final var y1 = FiscalYear.of(y);
            final var y2 = FiscalYear.of(y + 1);

            // when

            // then
            assertThat(y1).isNotEqualTo(y2);
        }
    }
}
