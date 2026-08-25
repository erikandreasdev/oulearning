package com.example.oulearning.budgeting.domain.exception;

import com.example.oulearning.budgeting.domain.model.BudgetingConstants;

public final class InvalidBudgetOperationException extends BudgetingException {

    public InvalidBudgetOperationException(final String message) {
        super(message);
    }

    public InvalidBudgetOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public static InvalidBudgetOperationException nullField(final String fieldName) {
        return new InvalidBudgetOperationException("%s cannot be null".formatted(fieldName));
    }

    public static InvalidBudgetOperationException blankField(final String fieldName) {
        return new InvalidBudgetOperationException("%s cannot be blank".formatted(fieldName));
    }

    public static InvalidBudgetOperationException nullOrBlank(final String fieldName) {
        return new InvalidBudgetOperationException("%s string cannot be null or blank".formatted(fieldName));
    }

    public static InvalidBudgetOperationException fiscalYearOutOfRange(final int min, final int max, final int actual) {
        return new InvalidBudgetOperationException(
                "Fiscal year must be between %d and %d: %d".formatted(min, max, actual));
    }

    public static InvalidBudgetOperationException nonPositiveId(final String fieldName, final long value) {
        return new InvalidBudgetOperationException(
                "%s must be strictly positive (at least %d): %d".formatted(fieldName, BudgetingConstants.MIN_ID, value));
    }

    public static InvalidBudgetOperationException invalidId(
            final String fieldName, final String value, final Throwable cause) {
        return new InvalidBudgetOperationException("Invalid %s format: %s".formatted(fieldName, value), cause);
    }
}
