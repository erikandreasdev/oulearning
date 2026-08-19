package com.example.oulearning.budgeting.domain.exception;

/** Exception thrown when a budget operation violates domain invariants. */
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

    public static InvalidBudgetOperationException invalidUuid(final String value) {
        return new InvalidBudgetOperationException("Invalid UUID format: %s".formatted(value));
    }
}
