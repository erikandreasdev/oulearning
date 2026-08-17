package com.example.oulearning.shared.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

/**
 * Value object representing a monetary amount backed by the Moneta JSR 354 library with EUR as the default currency.
 *
 * @param value the underlying {@link MonetaryAmount}
 */
public record Money(MonetaryAmount value) implements Comparable<Money> {

    public static final CurrencyUnit DEFAULT_CURRENCY = Monetary.getCurrency("EUR");

    /**
     * Compact constructor enforcing non-null and non-negative constraints.
     */
    public Money {
        if (value == null) {
            throw new InvalidMoneyException("Monetary amount cannot be null");
        }
        if (value.isNegative()) {
            throw new InvalidMoneyException("Monetary amount cannot be negative: %s".formatted(value));
        }
    }

    /**
     * Factory method creating a {@link Money} instance with specified amount and currency.
     *
     * @param amount   the monetary amount as {@link BigDecimal}
     * @param currency the {@link CurrencyUnit}
     * @return a validated {@link Money} instance
     */
    public static Money of(BigDecimal amount, CurrencyUnit currency) {
        if (amount == null) {
            throw new InvalidMoneyException("Amount cannot be null");
        }
        if (currency == null) {
            throw new InvalidMoneyException("Currency cannot be null");
        }
        final var scaledAmount = amount.setScale(2, RoundingMode.HALF_EVEN);
        return new Money(org.javamoney.moneta.Money.of(scaledAmount, currency));
    }

    /**
     * Factory method creating a {@link Money} instance with specified amount in EUR.
     *
     * @param amount the monetary amount as {@link BigDecimal}
     * @return a validated {@link Money} instance in EUR
     */
    public static Money of(BigDecimal amount) {
        return of(amount, DEFAULT_CURRENCY);
    }

    /**
     * Factory method creating a {@link Money} instance with double value in EUR.
     *
     * @param amount the double monetary amount
     * @return a validated {@link Money} instance in EUR
     */
    public static Money of(double amount) {
        return of(BigDecimal.valueOf(amount), DEFAULT_CURRENCY);
    }

    /**
     * Factory method creating a {@link Money} instance with long value in EUR.
     *
     * @param amount the long monetary amount
     * @return a validated {@link Money} instance in EUR
     */
    public static Money of(long amount) {
        return of(BigDecimal.valueOf(amount), DEFAULT_CURRENCY);
    }

    /**
     * Factory method creating a {@link Money} instance in EUR.
     *
     * @param amount the {@link BigDecimal} amount
     * @return a validated {@link Money} instance in EUR
     */
    public static Money euros(BigDecimal amount) {
        return of(amount, DEFAULT_CURRENCY);
    }

    /**
     * Factory method creating a {@link Money} instance in EUR.
     *
     * @param amount the double amount
     * @return a validated {@link Money} instance in EUR
     */
    public static Money euros(double amount) {
        return of(BigDecimal.valueOf(amount), DEFAULT_CURRENCY);
    }

    /**
     * Factory method creating a {@link Money} instance in EUR.
     *
     * @param amount the long amount
     * @return a validated {@link Money} instance in EUR
     */
    public static Money euros(long amount) {
        return of(BigDecimal.valueOf(amount), DEFAULT_CURRENCY);
    }

    /**
     * Factory method returning zero EUR money.
     *
     * @return a {@link Money} instance with value 0.00 EUR
     */
    public static Money zero() {
        return of(BigDecimal.ZERO, DEFAULT_CURRENCY);
    }

    /**
     * Factory method returning zero money in the specified currency.
     *
     * @param currency the {@link CurrencyUnit}
     * @return a {@link Money} instance with value 0.00 in the given currency
     */
    public static Money zero(CurrencyUnit currency) {
        return of(BigDecimal.ZERO, currency);
    }

    /**
     * Adds another monetary amount to this.
     *
     * @param other the other {@link Money}
     * @return a new {@link Money} representing the sum
     */
    public Money plus(Money other) {
        if (other == null) {
            throw new InvalidMoneyException("Cannot add null Money");
        }
        return new Money(this.value.add(other.value));
    }

    /**
     * Subtracts another monetary amount from this.
     *
     * @param other the other {@link Money}
     * @return a new {@link Money} representing the difference
     */
    public Money minus(Money other) {
        if (other == null) {
            throw new InvalidMoneyException("Cannot subtract null Money");
        }
        return new Money(this.value.subtract(other.value));
    }

    /**
     * Checks if this money amount is zero.
     *
     * @return {@code true} if amount is zero
     */
    public boolean isZero() {
        return value.isZero();
    }

    /**
     * Checks if this money amount is strictly positive.
     *
     * @return {@code true} if amount > 0
     */
    public boolean isPositive() {
        return value.isPositive();
    }

    /**
     * Gets the numeric amount as {@link BigDecimal}.
     *
     * @return the {@link BigDecimal} amount
     */
    public BigDecimal amount() {
        return value.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Gets the currency unit.
     *
     * @return the {@link CurrencyUnit}
     */
    public CurrencyUnit currency() {
        return value.getCurrency();
    }

    @Override
    public int compareTo(Money other) {
        Objects.requireNonNull(other, "Cannot compare Money with null");
        return this.value.compareTo(other.value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var money = (Money) o;
        return this.value.isEqualTo(money.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency().getCurrencyCode(), amount());
    }

    @Override
    public String toString() {
        return "%s %.2f".formatted(currency().getCurrencyCode(), amount());
    }
}
