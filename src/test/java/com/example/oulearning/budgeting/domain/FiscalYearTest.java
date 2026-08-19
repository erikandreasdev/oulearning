package com.example.oulearning.budgeting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
        @ValueSource(ints = {1900, 2026, 2099, 3000})
        @DisplayName("should create FiscalYear when valid year provided")
        void should_createFiscalYear_when_validYearProvided(int year) {
            FiscalYear fiscalYear = FiscalYear.of(year);

            assertThat(fiscalYear.value()).isEqualTo(year);
            assertThat(fiscalYear.toString()).isEqualTo(String.valueOf(year));
        }

        @ParameterizedTest
        @ValueSource(ints = {-1, 0, 1899, 3001, 9999})
        @DisplayName("should throw InvalidBudgetOperationException when year is out of range")
        void should_throwException_when_yearOutOfRange(int invalidYear) {
            assertThatThrownBy(() -> FiscalYear.of(invalidYear))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("Fiscal year must be between 1900 and 3000");
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when years match")
        void should_beEqual_when_yearsMatch() {
            FiscalYear year1 = FiscalYear.of(2026);
            FiscalYear year2 = FiscalYear.of(2026);

            assertThat(year1).isEqualTo(year2);
            assertThat(year1.hashCode()).isEqualTo(year2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when years differ")
        void should_notBeEqual_when_yearsDiffer() {
            FiscalYear year1 = FiscalYear.of(2026);
            FiscalYear year2 = FiscalYear.of(2027);

            assertThat(year1).isNotEqualTo(year2);
        }
    }
}
