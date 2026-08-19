package com.example.oulearning.training.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
        @DisplayName("given BigDecimal and currency, when creating Cost, then cost is created successfully")
        void givenBigDecimalAndCurrency_whenCreatingCost_thenCostIsCreatedSuccessfully() {

            final var rawAmount = TrainingTestFactory.randomBigDecimalCostAmount().add(BigDecimal.ONE);


            final var cost = Cost.of(rawAmount, "EUR");


            final var expectedAmount = rawAmount.setScale(2, RoundingMode.HALF_EVEN);
            assertThat(cost.amount()).isEqualTo(expectedAmount);
            assertThat(cost.currency()).isEqualTo("EUR");
            assertThat(cost.toString()).isEqualTo("%s EUR".formatted(expectedAmount));
        }

        @Test
        @DisplayName("given double and currency, when creating Cost, then cost is created successfully")
        void givenDoubleAndCurrency_whenCreatingCost_thenCostIsCreatedSuccessfully() {

            final var rawAmount = TrainingTestFactory.randomDoubleCostAmount();


            final var cost = Cost.of(rawAmount, "usd");


            final var expectedAmount = BigDecimal.valueOf(rawAmount).setScale(2, RoundingMode.HALF_EVEN);
            assertThat(cost.amount()).isEqualTo(expectedAmount);
            assertThat(cost.currency()).isEqualTo("USD");
        }

        @Test
        @DisplayName("given double without currency, when creating Cost, then default to EUR")
        void givenDoubleWithoutCurrency_whenCreatingCost_thenDefaultToEur() {

            final var rawAmount = TrainingTestFactory.randomDoubleCostAmount();


            final var cost = Cost.of(rawAmount);


            final var expectedAmount = BigDecimal.valueOf(rawAmount).setScale(2, RoundingMode.HALF_EVEN);
            assertThat(cost.amount()).isEqualTo(expectedAmount);
            assertThat(cost.currency()).isEqualTo("EUR");
        }

        @Test
        @DisplayName("given zero factory, when creating zero Cost, then amount is zero")
        void givenZeroFactory_whenCreatingZeroCost_thenAmountIsZero() {



            final var cost = Cost.zero();


            assertThat(cost.amount()).isEqualTo(new BigDecimal("0.00"));
            assertThat(cost.currency()).isEqualTo("EUR");
        }

        @Test
        @DisplayName("given null amount, when creating Cost, then throw InvalidTrainingOperationException")
        void givenNullAmount_whenCreatingCost_thenThrowInvalidTrainingOperationException() {





            assertThatThrownBy(() -> new Cost(null, "EUR"))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("amount cannot be null");
        }

        @Test
        @DisplayName("given negative amount, when creating Cost, then throw InvalidTrainingOperationException")
        void givenNegativeAmount_whenCreatingCost_thenThrowInvalidTrainingOperationException() {

            final var neg = -TrainingTestFactory.randomDoubleCostAmount();




            assertThatThrownBy(() -> Cost.of(neg, "EUR"))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be negative");
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "XYZ123"})
        @DisplayName("given invalid currency, when creating Cost, then throw InvalidTrainingOperationException")
        void givenInvalidCurrency_whenCreatingCost_thenThrowInvalidTrainingOperationException(final String invalidCurrency) {

            final var amount = TrainingTestFactory.randomDoubleCostAmount();




            assertThatThrownBy(() -> Cost.of(amount, invalidCurrency))
                    .isInstanceOf(InvalidTrainingOperationException.class);
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical amounts and currencies, when comparing Cost, then they are equal")
        void givenIdenticalAmountsAndCurrencies_whenComparingCost_thenTheyAreEqual() {

            final var amount = TrainingTestFactory.randomDoubleCostAmount();
            final var c1 = Cost.of(amount, "EUR");
            final var c2 = Cost.of(BigDecimal.valueOf(amount), "eur");




            assertThat(c1).isEqualTo(c2);
            assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
        }

        @Test
        @DisplayName("given different amounts or currencies, when comparing Cost, then they are not equal")
        void givenDifferentAmountsOrCurrencies_whenComparingCost_thenTheyAreNotEqual() {

            final var c1 = TrainingTestFactory.randomCost();
            final var c2 = Cost.of(c1.amount(), "USD");
            final var c3 = Cost.of(c1.amount().add(BigDecimal.TEN), c1.currency());




            assertThat(c1).isNotEqualTo(c2);
            assertThat(c1).isNotEqualTo(c3);
        }
    }
}
