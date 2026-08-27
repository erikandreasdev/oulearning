package com.example.oulearning.training.domain.model;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.budgeting.domain.model.BudgetingConstants;
import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import java.math.BigDecimal;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class CostTest {

    @Nested
    @DisplayName("Creation and Formatting")
    class CreationAndFormatting {

        @Test
        @DisplayName("given positive double amount and lowercase currency, when creating Cost, then create successfully and format uppercase")
        void givenPositiveDoubleAmountAndLowercaseCurrency_whenCreatingCost_thenCreateSuccessfully() {
            // given
            final var amount = TrainingTestFactory.randomDoubleCostAmount();

            // when
            final var cost = Cost.of(amount, BudgetingConstants.DEFAULT_CURRENCY.toLowerCase());

            // then
            assertThat(cost.amount()).isEqualByComparingTo(BigDecimal.valueOf(amount));
            assertThat(cost.currency()).isEqualTo(BudgetingConstants.DEFAULT_CURRENCY);
            assertThat(cost.toString()).contains(BudgetingConstants.DEFAULT_CURRENCY);
        }

        @Test
        @DisplayName("given BigDecimal with scale, when creating Cost, then scale to two decimal places")
        void givenBigDecimalWithScale_whenCreatingCost_thenScaleToTwoDecimalPlaces() {
            // given
            final var costAmount = BigDecimal.valueOf(123.456);

            // when
            final var cost = Cost.of(costAmount, BudgetingConstants.DEFAULT_CURRENCY);

            // then
            assertThat(cost.amount()).isEqualTo(new BigDecimal("123.46"));
        }

        @Test
        @DisplayName("given double with default currency, when creating Cost, then default to EUR")
        void givenDoubleWithDefaultCurrency_whenCreatingCost_thenDefaultToEur() {
            // given
            final var amount = TrainingTestFactory.randomDoubleCostAmount();

            // when
            final var cost = Cost.of(amount);

            // then
            assertThat(cost.currency()).isEqualTo(BudgetingConstants.DEFAULT_CURRENCY);
        }

        @Test
        @DisplayName("given BigDecimal with default currency, when creating Cost, then default to EUR")
        void givenBigDecimalWithDefaultCurrency_whenCreatingCost_thenDefaultToEur() {
            // given
            final var amount = TrainingTestFactory.randomBigDecimalCostAmount();

            // when
            final var cost = Cost.of(amount);

            // then
            assertThat(cost.currency()).isEqualTo(BudgetingConstants.DEFAULT_CURRENCY);
        }

        @Test
        @DisplayName("given zero factory method with default currency, when creating Cost, then amount is zero in EUR")
        void givenZeroDefault_whenCreatingCost_thenAmountIsZeroInEur() {
            // given

            // when
            final var cost = Cost.zero();

            // then
            assertThat(cost.amount()).isEqualTo(new BigDecimal("0.00"));
            assertThat(cost.currency()).isEqualTo(BudgetingConstants.DEFAULT_CURRENCY);
        }

        @Test
        @DisplayName("given null amount, when creating Cost, then throw InvalidTrainingOperationException")
        void givenNullAmount_whenCreatingCost_thenThrowInvalidTrainingOperationException() {
            // given

            // when

            // then
            assertThatThrownBy(() -> new Cost(null, BudgetingConstants.DEFAULT_CURRENCY))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("amount cannot be null");
        }

        @Test
        @DisplayName("given negative amount, when creating Cost, then throw InvalidTrainingOperationException")
        void givenNegativeAmount_whenCreatingCost_thenThrowInvalidTrainingOperationException() {
            // given
            final var neg = -TrainingTestFactory.randomDoubleCostAmount();

            // when

            // then
            assertThatThrownBy(() -> Cost.of(neg, BudgetingConstants.DEFAULT_CURRENCY))
                    .isInstanceOf(InvalidTrainingOperationException.class)
                    .hasMessageContaining("cannot be negative");
        }

        @Test
        @DisplayName("given invalid currency, when creating Cost, then throw InvalidTrainingOperationException")
        void givenInvalidCurrency_whenCreatingCost_thenThrowInvalidTrainingOperationException() {
            // given
            final var amount = TrainingTestFactory.randomDoubleCostAmount();
            final var invalidCurrency = Instancio.gen().string().length(6).get();

            // when

            // then
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
            // given
            final var amount = TrainingTestFactory.randomDoubleCostAmount();
            final var c1 = Cost.of(amount, BudgetingConstants.DEFAULT_CURRENCY);
            final var c2 = Cost.of(BigDecimal.valueOf(amount), BudgetingConstants.DEFAULT_CURRENCY.toLowerCase());

            // when

            // then
            assertThat(c1).isEqualTo(c2).hasSameHashCodeAs(c2);
        }

        @Test
        @DisplayName("given different amounts, when comparing Cost, then they are not equal")
        void givenDifferentAmounts_whenComparingCost_thenTheyAreNotEqual() {
            // given
            final var c1 = TrainingTestFactory.randomCost();
            final var c2 = Cost.of(c1.amount().add(BigDecimal.TEN), c1.currency());

            // when

            // then
            assertThat(c1).isNotEqualTo(c2);
        }
    }
}
