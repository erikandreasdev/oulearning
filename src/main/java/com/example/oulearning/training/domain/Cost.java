package com.example.oulearning.training.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * Value object representing training cost.
 *
 * @param amount the numeric cost amount
 * @param currency the ISO currency code
 */
public record Cost(BigDecimal amount, String currency) {

    public static final int DEFAULT_SCALE = 2;

    public Cost {
        if (amount == null) {
            throw new InvalidTrainingOperationException("Cost amount cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new InvalidTrainingOperationException("Currency cannot be null or blank");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTrainingOperationException("Cost amount cannot be negative: " + amount);
        }

        currency = currency.strip().toUpperCase();
        try {
            Currency.getInstance(currency);
        } catch (IllegalArgumentException e) {
            throw new InvalidTrainingOperationException("Invalid currency code: " + currency);
        }

        amount = amount.setScale(DEFAULT_SCALE, RoundingMode.HALF_UP);
    }

    public static Cost of(BigDecimal amount, String currency) {
        return new Cost(amount, currency);
    }

    public static Cost of(double amount, String currency) {
        return new Cost(BigDecimal.valueOf(amount), currency);
    }

    public static Cost zero(String currency) {
        return new Cost(BigDecimal.ZERO, currency);
    }

    @Override
    public String toString() {
        return amount + " " + currency;
    }
}
