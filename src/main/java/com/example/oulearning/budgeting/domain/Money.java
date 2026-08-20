package com.example.oulearning.budgeting.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

public record Money(MonetaryAmount monetaryAmount) {

    private static final CurrencyUnit EUR = Monetary.getCurrency(BudgetingConstants.DEFAULT_CURRENCY);
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public Money {
        monetaryAmount = BudgetingGuard.requireNonNull(monetaryAmount, "MonetaryAmount");
    }

    public static Money of(final BigDecimal amount) {
        BudgetingGuard.requireNonNull(amount, "Money amount");
        final var scaledAmount = amount.setScale(BudgetingConstants.MONEY_SCALE, ROUNDING_MODE);
        return new Money(org.javamoney.moneta.Money.of(scaledAmount, EUR));
    }

    public static Money of(final double amount) {
        return of(BigDecimal.valueOf(amount));
    }

    public static Money zero() {
        return of(BigDecimal.ZERO);
    }

    public BigDecimal amount() {
        return monetaryAmount
                .getNumber()
                .numberValue(BigDecimal.class)
                .setScale(BudgetingConstants.MONEY_SCALE, ROUNDING_MODE);
    }

    public String currency() {
        return BudgetingConstants.DEFAULT_CURRENCY;
    }

    public Money add(final Money other) {
        BudgetingGuard.requireNonNull(other, "Money to add");
        return new Money(monetaryAmount.add(other.monetaryAmount));
    }

    public Money subtract(final Money other) {
        BudgetingGuard.requireNonNull(other, "Money to subtract");
        return new Money(monetaryAmount.subtract(other.monetaryAmount));
    }

    private static final String MONEY_TO_COMPARE = "Money to compare";

    public boolean isGreaterThan(final Money other) {
        BudgetingGuard.requireNonNull(other, MONEY_TO_COMPARE);
        return monetaryAmount.isGreaterThan(other.monetaryAmount);
    }

    public boolean isGreaterThanOrEqualTo(final Money other) {
        BudgetingGuard.requireNonNull(other, MONEY_TO_COMPARE);
        return monetaryAmount.isGreaterThanOrEqualTo(other.monetaryAmount);
    }

    public boolean isLessThan(final Money other) {
        BudgetingGuard.requireNonNull(other, MONEY_TO_COMPARE);
        return monetaryAmount.isLessThan(other.monetaryAmount);
    }

    public boolean isLessThanOrEqualTo(final Money other) {
        BudgetingGuard.requireNonNull(other, MONEY_TO_COMPARE);
        return monetaryAmount.isLessThanOrEqualTo(other.monetaryAmount);
    }

    public boolean isNegative() {
        return monetaryAmount.isNegative();
    }

    public boolean isZero() {
        return monetaryAmount.isZero();
    }

    public boolean isPositive() {
        return monetaryAmount.isPositive();
    }

    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof final Money money && amount().compareTo(money.amount()) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount(), currency());
    }

    @Override
    public String toString() {
        return "%s %s".formatted(amount(), currency());
    }
}
