package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CostTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("should create Cost from BigDecimal and currency")
        void should_createCost_fromBigDecimal() {
            Cost cost = Cost.of(new BigDecimal("1500.5"), "EUR");

            assertThat(cost.amount()).isEqualTo(new BigDecimal("1500.50"));
            assertThat(cost.currency()).isEqualTo("EUR");
            assertThat(cost.toString()).isEqualTo("1500.50 EUR");
        }

        @Test
        @DisplayName("should create Cost from double and currency")
        void should_createCost_fromDouble() {
            Cost cost = Cost.of(200.0, "usd");

            assertThat(cost.amount()).isEqualTo(new BigDecimal("200.00"));
            assertThat(cost.currency()).isEqualTo("USD");
        }

        @Test
        @DisplayName("should create zero Cost")
        void should_createZeroCost() {
            Cost cost = Cost.zero("EUR");

            assertThat(cost.amount()).isEqualTo(new BigDecimal("0.00"));
        }

        @Test
        @DisplayName("should throw InvalidTrainingOperationException when amount is null")
        void should_throwException_when_amountIsNull() {
            assertThatThrownBy(() -> new Cost(null, "EUR"))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("amount cannot be null");
        }

        @Test
        @DisplayName("should throw InvalidTrainingOperationException when amount is negative")
        void should_throwException_when_amountIsNegative() {
            assertThatThrownBy(() -> Cost.of(-10.0, "EUR"))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be negative");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "XYZ123"})
        @DisplayName("should throw InvalidTrainingOperationException when currency is invalid")
        void should_throwException_when_currencyIsInvalid(String invalidCurrency) {
            assertThatThrownBy(() -> Cost.of(100.0, invalidCurrency))
                    .isInstanceOf(InvalidTrainingOperationException.class);
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when amount and currency match")
        void should_beEqual_when_amountAndCurrencyMatch() {
            Cost c1 = Cost.of(500.0, "EUR");
            Cost c2 = Cost.of(new BigDecimal("500.00"), "eur");

            assertThat(c1).isEqualTo(c2);
            assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when amounts or currencies differ")
        void should_notBeEqual_when_amountsOrCurrenciesDiffer() {
            Cost c1 = Cost.of(500.0, "EUR");
            Cost c2 = Cost.of(500.0, "USD");
            Cost c3 = Cost.of(600.0, "EUR");

            assertThat(c1).isNotEqualTo(c2);
            assertThat(c1).isNotEqualTo(c3);
        }
    }
}
