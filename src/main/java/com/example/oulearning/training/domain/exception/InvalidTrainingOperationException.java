package com.example.oulearning.training.domain.exception;


public final class InvalidTrainingOperationException extends TrainingException {

    public InvalidTrainingOperationException(final String message) {
        super(message);
    }

    public InvalidTrainingOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public static InvalidTrainingOperationException nullField(final String fieldName) {
        return new InvalidTrainingOperationException("%s cannot be null".formatted(fieldName));
    }

    public static InvalidTrainingOperationException blankField(final String fieldName) {
        return new InvalidTrainingOperationException("%s cannot be blank".formatted(fieldName));
    }

    public static InvalidTrainingOperationException nullOrBlank(final String fieldName) {
        return new InvalidTrainingOperationException("%s string cannot be null or blank".formatted(fieldName));
    }

    public static InvalidTrainingOperationException lengthOutOfRange(
            final String fieldName, final int min, final int max, final String actual) {
        return new InvalidTrainingOperationException(
                "%s length must be between %d and %d characters: %s".formatted(fieldName, min, max, actual));
    }

    public static InvalidTrainingOperationException invalidUuid(final String value) {
        return new InvalidTrainingOperationException("Invalid UUID format: %s".formatted(value));
    }

    public static InvalidTrainingOperationException negativeCost(final Object amount) {
        return new InvalidTrainingOperationException("Cost amount cannot be negative: %s".formatted(amount));
    }

    public static InvalidTrainingOperationException invalidCurrency(final String currency) {
        return new InvalidTrainingOperationException("Invalid currency code: %s".formatted(currency));
    }

    public static InvalidTrainingOperationException invalidHours(final int min, final int actual) {
        return new InvalidTrainingOperationException(
                "Training hours must be strictly positive (at least %d): %d".formatted(min, actual));
    }

    public static InvalidTrainingOperationException invalidPhoneFormat(
            final String raw, final int minDigits, final int maxDigits) {
        return new InvalidTrainingOperationException(
                "Invalid phone number format: %s. Must contain %d-%d digits.".formatted(raw, minDigits, maxDigits));
    }

    public static InvalidTrainingOperationException invalidDateRange(final Object startDate, final Object endDate) {
        return new InvalidTrainingOperationException(
                "End date (%s) cannot be before start date (%s)".formatted(endDate, startDate));
    }
}
