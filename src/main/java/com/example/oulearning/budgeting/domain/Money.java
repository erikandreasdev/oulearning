package com.example.oulearning.budgeting.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Value object representing monetary value with an amount and currency.
 *
 * @param amount the numeric amount
 * @param currency the ISO currency code (e.g., "USD", "EUR")
 */
public record Money(BigDecimal amount, String currency) {

    public static final int DEFAULT_SCALE = 2;

    public Money {
        if (amount == null) {
            throw new InvalidBudgetOperationException("Amount cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new InvalidBudgetOperationException("Currency cannot be null or blank");
        }

        currency = currency.strip().toUpperCase();
        try {
            Currency.getInstance(currency);
        } catch (IllegalArgumentException e) {
            throw new InvalidBudgetOperationException("Invalid currency code: " + currency);
        }

        amount = amount.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    public static Money of(BigDecimal amount, String currency) {
        return new Money(amount, currency);
    }

    public static Money of(double amount, String currency) {
        return new Money(BigDecimal.valueOf(amount), currency);
    }

    public static Money of(String amount, String currency) {
        if (amount == null || amount.isBlank()) {
            throw new InvalidBudgetOperationException("Amount string cannot be null or blank");
        }
        try {
            return new Money(new BigDecimal(amount.strip()), currency);
        } catch (NumberFormatException e) {
            throw new InvalidBudgetOperationException("Invalid amount format: " + amount);
        }
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isLessThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    public boolean isLessThanOrEqual(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) <= 0;
    }

    public boolean isZero() {
        return this.amount.compareTo(BigDecimal.ZERO) == 0;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) < 0;
    }

    private void validateSameCurrency(Money other) {
        Objects.requireNonNull(other, "Money operand cannot be null");
        if (!this.currency.equalsIgnoreCase(other.currency)) {
            throw new CurrencyMismatchException(
                    "Cannot perform monetary operation on different currencies: " + this.currency + " and " + other.currency);
        }
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
