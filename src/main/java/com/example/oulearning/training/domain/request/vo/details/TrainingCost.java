package com.example.oulearning.training.domain.request.vo.details;

import com.example.oulearning.training.domain.request.exception.InvalidTrainingRequestException;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value Object representing the financial cost of a training program.
 */
public record TrainingCost(BigDecimal amount, String currency) {

    public static final String DEFAULT_CURRENCY = "EUR";

    public TrainingCost {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidTrainingRequestException("Training cost cannot be negative: " + amount);
        }
        if (currency.isBlank() || currency.length() != 3) {
            throw new InvalidTrainingRequestException("Invalid currency code: " + currency);
        }
        currency = currency.trim().toUpperCase();
    }

    public static TrainingCost of(BigDecimal amount, String currency) {
        return new TrainingCost(amount, currency);
    }

    public static TrainingCost euros(BigDecimal amount) {
        return of(amount, DEFAULT_CURRENCY);
    }

    public static TrainingCost euros(double amount) {
        return of(BigDecimal.valueOf(amount), DEFAULT_CURRENCY);
    }

    public static TrainingCost zero() {
        return euros(BigDecimal.ZERO);
    }

    @Override
    public String toString() {
        return "%s %s".formatted(currency, amount);
    }
}
