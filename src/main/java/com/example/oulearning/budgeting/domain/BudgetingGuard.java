package com.example.oulearning.budgeting.domain;

import com.example.oulearning.budgeting.domain.exception.InvalidBudgetOperationException;
import java.util.UUID;

final class BudgetingGuard {

    private BudgetingGuard() {
    }

    static <T> T requireNonNull(final T value, final String fieldName) {
        if (value == null) {
            throw InvalidBudgetOperationException.nullField(fieldName);
        }
        return value;
    }

    static String requireNonBlank(final String value, final String fieldName) {
        final var notNull = requireNonNull(value, fieldName).strip();
        if (notNull.isBlank()) {
            throw InvalidBudgetOperationException.blankField(fieldName);
        }
        return notNull;
    }

    static UUID requireValidUuid(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw InvalidBudgetOperationException.nullOrBlank(fieldName);
        }
        try {
            return UUID.fromString(value.strip());
        } catch (final IllegalArgumentException e) {
            throw InvalidBudgetOperationException.invalidUuid(value, e);
        }
    }

    static int requireFiscalYearBetween(final int value, final int min, final int max) {
        if (value < min || value > max) {
            throw InvalidBudgetOperationException.fiscalYearOutOfRange(min, max, value);
        }
        return value;
    }
}
