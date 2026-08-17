package com.example.oulearning.budgeting.domain.budget;

import com.example.oulearning.budgeting.domain.budget.exception.InvalidMoneyException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;

/**
 * Value object representing monetary amounts with EUR as the default currency.
 * Backed by Moneta (JSR 354 reference implementation) for precision.
 */
public record Money(MonetaryAmount monetaryAmount) implements Comparable<Money> {

    public static final CurrencyUnit DEFAULT_CURRENCY = Monetary.getCurrency("EUR");

    public Money {
        if (monetaryAmount == null) {
            throw new InvalidMoneyException("MonetaryAmount cannot be null");
        }
        if (monetaryAmount.isNegative()) {
            throw new InvalidMoneyException("Money amount cannot be negative: " + monetaryAmount);
        }
        final var number =
                monetaryAmount.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
        monetaryAmount = org.javamoney.moneta.Money.of(number, monetaryAmount.getCurrency());
    }

    public static Money of(BigDecimal amount, CurrencyUnit currency) {
        if (amount == null) {
            throw new InvalidMoneyException("Amount cannot be null");
        }
        if (currency == null) {
            throw new InvalidMoneyException("Currency cannot be null");
        }
        final var scaled = amount.setScale(2, RoundingMode.HALF_UP);
        return new Money(org.javamoney.moneta.Money.of(scaled, currency));
    }

    public static Money euros(BigDecimal amount) {
        return of(amount, DEFAULT_CURRENCY);
    }

    public static Money euros(double amount) {
        return euros(BigDecimal.valueOf(amount));
    }

    public static Money zero(CurrencyUnit currency) {
        return of(BigDecimal.ZERO, currency);
    }

    public static Money zero() {
        return zero(DEFAULT_CURRENCY);
    }

    public Money plus(Money other) {
        Objects.requireNonNull(other, "Cannot add null Money");
        checkSameCurrency(other);
        return new Money(this.monetaryAmount.add(other.monetaryAmount));
    }

    public Money minus(Money other) {
        Objects.requireNonNull(other, "Cannot subtract null Money");
        checkSameCurrency(other);
        if (this.compareTo(other) < 0) {
            throw new InvalidMoneyException(
                    "Cannot subtract %s from %s: result would be negative".formatted(other, this));
        }
        return new Money(this.monetaryAmount.subtract(other.monetaryAmount));
    }

    public boolean isZero() {
        return this.monetaryAmount.isZero();
    }

    public CurrencyUnit currency() {
        return this.monetaryAmount.getCurrency();
    }

    public BigDecimal amount() {
        return this.monetaryAmount.getNumber().numberValue(BigDecimal.class).setScale(2, RoundingMode.HALF_UP);
    }

    private void checkSameCurrency(Money other) {
        if (!this.currency().equals(other.currency())) {
            throw new InvalidMoneyException(
                    "Currency mismatch: %s vs %s".formatted(this.currency(), other.currency()));
        }
    }

    @Override
    public int compareTo(Money other) {
        checkSameCurrency(other);
        return this.amount().compareTo(other.amount());
    }

    @Override
    public String toString() {
        return "%s %s".formatted(currency().getCurrencyCode(), amount());
    }
}
