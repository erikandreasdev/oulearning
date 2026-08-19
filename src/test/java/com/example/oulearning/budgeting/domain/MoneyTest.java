package com.example.oulearning.budgeting.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.budgeting.domain.exception.InvalidBudgetOperationException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Nested
    @DisplayName("Creation and Validation")
    class CreationAndValidation {

        @Test
        @DisplayName("given BigDecimal amount, when creating Money, then money has scaled amount and EUR currency")
        void givenBigDecimalAmount_whenCreatingMoney_thenMoneyHasScaledAmountAndEurCurrency() {

            final var rawAmount = BudgetingTestFactory.randomBigDecimalAmount();


            final var money = Money.of(rawAmount);


            final var expectedAmount = rawAmount.setScale(2, RoundingMode.HALF_EVEN);
            assertThat(money.amount()).isEqualTo(expectedAmount);
            assertThat(money.currency()).isEqualTo("EUR");
            assertThat(money.toString()).isEqualTo("%s EUR".formatted(expectedAmount));
        }

        @Test
        @DisplayName("given double amount, when creating Money, then money has correct amount and EUR currency")
        void givenDoubleAmount_whenCreatingMoney_thenMoneyHasCorrectAmountAndEurCurrency() {

            final var rawAmount = BudgetingTestFactory.randomDoubleAmount();


            final var money = Money.of(rawAmount);


            final var expectedAmount = BigDecimal.valueOf(rawAmount).setScale(2, RoundingMode.HALF_EVEN);
            assertThat(money.amount()).isEqualTo(expectedAmount);
            assertThat(money.currency()).isEqualTo("EUR");
        }

        @Test
        @DisplayName("given zero factory, when creating zero Money, then amount is zero and EUR currency")
        void givenZeroFactory_whenCreatingZeroMoney_thenAmountIsZeroAndEurCurrency() {



            final var money = Money.zero();


            assertThat(money.amount()).isEqualTo(new BigDecimal("0.00"));
            assertThat(money.currency()).isEqualTo("EUR");
            assertThat(money.isZero()).isTrue();
        }

        @Test
        @DisplayName("given null amount, when creating Money, then throw InvalidBudgetOperationException")
        void givenNullAmount_whenCreatingMoney_thenThrowInvalidBudgetOperationException() {





            assertThatThrownBy(() -> Money.of((BigDecimal) null))
                    .isInstanceOf(InvalidBudgetOperationException.class)
                    .hasMessageContaining("Money amount cannot be null");
        }
    }

    @Nested
    @DisplayName("Arithmetic Operations")
    class ArithmeticOperations {

        @Test
        @DisplayName("given two Money amounts, when adding, then return summed Money amount")
        void givenTwoMoneyAmounts_whenAdding_thenReturnSummedMoneyAmount() {

            final var m1 = BudgetingTestFactory.randomMoney();
            final var m2 = BudgetingTestFactory.randomMoney();


            final var result = m1.add(m2);


            assertThat(result.amount()).isEqualTo(m1.amount().add(m2.amount()));
            assertThat(result.currency()).isEqualTo("EUR");
        }

        @Test
        @DisplayName("given two Money amounts, when subtracting, then return deducted Money amount")
        void givenTwoMoneyAmounts_whenSubtracting_thenReturnDeductedMoneyAmount() {

            final var m1 = BudgetingTestFactory.randomMoney();
            final var m2 = BudgetingTestFactory.randomMoney();


            final var result = m1.subtract(m2);


            assertThat(result.amount()).isEqualTo(m1.amount().subtract(m2.amount()));
            assertThat(result.currency()).isEqualTo("EUR");
        }
    }

    @Nested
    @DisplayName("Comparison and Predicates")
    class ComparisonAndPredicates {

        @Test
        @DisplayName("given various Money amounts, when comparing, then comparisons return expected boolean values")
        void givenVariousMoneyAmounts_whenComparing_thenComparisonsReturnExpectedValues() {

            final var smallerAmount = BudgetingTestFactory.randomDoubleAmount();
            final var largerAmount = smallerAmount + 100.0;
            final var mSmall = Money.of(smallerAmount);
            final var mLarg = Money.of(largerAmount);
            final var mLargCopy = Money.of(largerAmount);




            assertThat(mLarg.isGreaterThan(mSmall)).isTrue();
            assertThat(mSmall.isLessThan(mLarg)).isTrue();
            assertThat(mLarg.isGreaterThanOrEqualTo(mLargCopy)).isTrue();
            assertThat(mLarg.isLessThanOrEqualTo(mLargCopy)).isTrue();
        }

        @Test
        @DisplayName("given positive, negative, and zero Money, when checking sign predicates, then correct flags returned")
        void givenDifferentSignedMoney_whenCheckingSignPredicates_thenCorrectFlagsReturned() {

            final var posAmount = BudgetingTestFactory.randomDoubleAmount();
            final var negAmount = -posAmount;
            final var positive = Money.of(posAmount);
            final var negative = Money.of(negAmount);
            final var zero = Money.zero();




            assertThat(positive.isPositive()).isTrue();
            assertThat(negative.isNegative()).isTrue();
            assertThat(zero.isZero()).isTrue();
        }
    }

    @Nested
    @DisplayName("Value Object Semantics")
    class ValueObjectSemantics {

        @Test
        @DisplayName("given identical amounts, when comparing Money, then they are equal")
        void givenIdenticalAmounts_whenComparingMoney_thenTheyAreEqual() {

            final var amount = BudgetingTestFactory.randomBigDecimalAmount();
            final var m1 = Money.of(amount);
            final var m2 = Money.of(amount);




            assertThat(m1).isEqualTo(m2);
            assertThat(m1.hashCode()).isEqualTo(m2.hashCode());
        }

        @Test
        @DisplayName("given different amounts, when comparing Money, then they are not equal")
        void givenDifferentAmounts_whenComparingMoney_thenTheyAreNotEqual() {

            final var m1 = BudgetingTestFactory.randomMoney();
            final var m2 = Money.of(m1.amount().add(BigDecimal.TEN));




            assertThat(m1).isNotEqualTo(m2);
        }
    }
}
