package com.example.oulearning.budgeting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.budgeting.domain.exception.InvalidBudgetOperationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FiscalYearTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @ParameterizedTest
        @ValueSource(ints = {1900, 2024, 2026, 3000})
        @DisplayName("given valid year within bounds, when creating FiscalYear, then create successfully")
        void givenValidYearWithinBounds_whenCreatingFiscalYear_thenCreateSuccessfully(final int year) {



            final var fiscalYear = FiscalYear.of(year);


            assertThat(fiscalYear.value()).isEqualTo(year);
            assertThat(fiscalYear.toString()).isEqualTo(String.valueOf(year));
        }

        @ParameterizedTest
        @ValueSource(ints = {1899, 0, -2024, 3001})
        @DisplayName("given year out of bounds, when creating FiscalYear, then throw InvalidBudgetOperationException")
        void givenYearOutOfBounds_whenCreatingFiscalYear_thenThrowInvalidBudgetOperationException(final int invalidYear) {





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

            final var y = BudgetingTestFactory.randomFiscalYearValue();
            final var y1 = FiscalYear.of(y);
            final var y2 = FiscalYear.of(y);




            assertThat(y1).isEqualTo(y2);
            assertThat(y1.hashCode()).isEqualTo(y2.hashCode());
        }

        @Test
        @DisplayName("given different years, when comparing FiscalYear, then they are not equal")
        void givenDifferentYears_whenComparingFiscalYear_thenTheyAreNotEqual() {

            final var y = BudgetingTestFactory.randomFiscalYearValue();
            final var y1 = FiscalYear.of(y);
            final var y2 = FiscalYear.of(y + 1);




            assertThat(y1).isNotEqualTo(y2);
        }
    }
}
