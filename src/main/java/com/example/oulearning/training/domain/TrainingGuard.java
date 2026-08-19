package com.example.oulearning.training.domain;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

final class TrainingGuard {

    private TrainingGuard() {}

    static <T> T requireNonNull(final T value, final String fieldName) {
        if (value == null) {
            throw InvalidTrainingOperationException.nullField(fieldName);
        }
        return value;
    }

    static String requireNonBlank(final String value, final String fieldName) {
        final var notNull = requireNonNull(value, fieldName).strip();
        if (notNull.isBlank()) {
            throw InvalidTrainingOperationException.blankField(fieldName);
        }
        return notNull;
    }

    static String requireLengthBetween(
            final String value, final String fieldName, final int min, final int max) {
        final var stripped = requireNonBlank(value, fieldName);
        if (stripped.length() < min || stripped.length() > max) {
            throw InvalidTrainingOperationException.lengthOutOfRange(fieldName, min, max, stripped);
        }
        return stripped;
    }

    static UUID requireValidUuid(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw InvalidTrainingOperationException.nullOrBlank(fieldName);
        }
        try {
            return UUID.fromString(value.strip());
        } catch (final IllegalArgumentException e) {
            throw InvalidTrainingOperationException.invalidUuid(value);
        }
    }

    static BigDecimal requireNonNegativeCost(final BigDecimal amount) {
        requireNonNull(amount, "Cost amount");
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw InvalidTrainingOperationException.negativeCost(amount);
        }
        return amount;
    }

    static String requireValidCurrency(final String currency) {
        final var stripped = requireNonBlank(currency, "Currency").toUpperCase();
        try {
            Currency.getInstance(stripped);
        } catch (final IllegalArgumentException e) {
            throw InvalidTrainingOperationException.invalidCurrency(stripped);
        }
        return stripped;
    }

    static int requireHoursAtLeast(final int value, final int minHours) {
        if (value < minHours) {
            throw InvalidTrainingOperationException.invalidHours(minHours, value);
        }
        return value;
    }
}
