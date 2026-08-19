package com.example.oulearning.budgeting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MoneyTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("should create Money from BigDecimal and currency")
        void should_createMoney_fromBigDecimal() {
            Money money = Money.of(new BigDecimal("100.5"), "EUR");

            assertThat(money.amount()).isEqualTo(new BigDecimal("100.50"));
            assertThat(money.currency()).isEqualTo("EUR");
            assertThat(money.toString()).isEqualTo("100.50 EUR");
        }

        @Test
        @DisplayName("should create Money from double and currency")
        void should_createMoney_fromDouble() {
            Money money = Money.of(250.0, "usd");

            assertThat(money.amount()).isEqualTo(new BigDecimal("250.00"));
            assertThat(money.currency()).isEqualTo("USD");
        }

        @Test
        @DisplayName("should create Money from string amount and currency")
        void should_createMoney_fromString() {
            Money money = Money.of(" 49.99 ", "GBP");

            assertThat(money.amount()).isEqualTo(new BigDecimal("49.99"));
            assertThat(money.currency()).isEqualTo("GBP");
        }

        @Test
        @DisplayName("should create zero Money")
        void should_createZeroMoney() {
            Money zero = Money.zero("USD");

            assertThat(zero.amount()).isEqualTo(new BigDecimal("0.00"));
            assertThat(zero.isZero()).isTrue();
        }

        @Test
        @DisplayName("should throw InvalidBudgetOperationException when amount is null")
        void should_throwException_when_amountIsNull() {
            assertThatThrownBy(() -> new Money(null, "EUR"))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("Amount cannot be null");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "INVALID_CURRENCY", "123"})
        @DisplayName("should throw InvalidBudgetOperationException when currency is invalid")
        void should_throwException_when_currencyIsInvalid(String invalidCurrency) {
            assertThatThrownBy(() -> new Money(BigDecimal.TEN, invalidCurrency))
                    .isInstanceOf(InvalidBudgetOperationException.class);
        }
    }

    @Nested
    @DisplayName("Arithmetic Operations")
    class ArithmeticOperations {

        @Test
        @DisplayName("should add money of same currency")
        void should_addMoney_ofSameCurrency() {
            Money m1 = Money.of(100.0, "EUR");
            Money m2 = Money.of(50.25, "EUR");

            Money result = m1.add(m2);

            assertThat(result).isEqualTo(Money.of(150.25, "EUR"));
        }

        @Test
        @DisplayName("should subtract money of same currency")
        void should_subtractMoney_ofSameCurrency() {
            Money m1 = Money.of(100.0, "EUR");
            Money m2 = Money.of(40.0, "EUR");

            Money result = m1.subtract(m2);

            assertThat(result).isEqualTo(Money.of(60.0, "EUR"));
        }

        @Test
        @DisplayName("should throw CurrencyMismatchException on add with different currencies")
        void should_throwException_onAddDifferentCurrencies() {
            Money eur = Money.of(100.0, "EUR");
            Money usd = Money.of(100.0, "USD");

            assertThatThrownBy(() -> eur.add(usd))
                    .isInstanceOf(CurrencyMismatchException.class)
                    .hasMessageContaining("Cannot perform monetary operation on different currencies");
        }

        @Test
        @DisplayName("should throw CurrencyMismatchException on subtract with different currencies")
        void should_throwException_onSubtractDifferentCurrencies() {
            Money eur = Money.of(100.0, "EUR");
            Money usd = Money.of(100.0, "USD");

            assertThatThrownBy(() -> eur.subtract(usd))
                    .isInstanceOf(CurrencyMismatchException.class);
        }
    }

    @Nested
    @DisplayName("Comparison and Predicates")
    class ComparisonAndPredicates {

        @Test
        @DisplayName("should correctly compare money values")
        void should_compareMoneyValues() {
            Money smaller = Money.of(50.0, "EUR");
            Money equal = Money.of(100.0, "EUR");
            Money larger = Money.of(100.0, "EUR");
            Money biggest = Money.of(150.0, "EUR");

            assertThat(biggest.isGreaterThan(equal)).isTrue();
            assertThat(equal.isGreaterThan(biggest)).isFalse();
            assertThat(equal.isGreaterThanOrEqual(larger)).isTrue();
            assertThat(smaller.isLessThan(equal)).isTrue();
            assertThat(equal.isLessThanOrEqual(larger)).isTrue();
        }

        @Test
        @DisplayName("should test zero, positive, negative predicates")
        void should_testPredicates() {
            Money zero = Money.of(0.0, "USD");
            Money positive = Money.of(10.0, "USD");
            Money negative = Money.of(-5.0, "USD");

            assertThat(zero.isZero()).isTrue();
            assertThat(positive.isPositive()).isTrue();
            assertThat(negative.isNegative()).isTrue();
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("should be equal when amounts and currencies match")
        void should_beEqual_when_amountsAndCurrenciesMatch() {
            Money m1 = Money.of(100.0, "EUR");
            Money m2 = Money.of(new BigDecimal("100.00"), "eur");

            assertThat(m1).isEqualTo(m2);
            assertThat(m1.hashCode()).isEqualTo(m2.hashCode());
        }

        @Test
        @DisplayName("should not be equal when amounts or currencies differ")
        void should_notBeEqual_when_amountsOrCurrenciesDiffer() {
            Money m1 = Money.of(100.0, "EUR");
            Money m2 = Money.of(100.0, "USD");
            Money m3 = Money.of(101.0, "EUR");

            assertThat(m1).isNotEqualTo(m2);
            assertThat(m1).isNotEqualTo(m3);
        }
    }
}
