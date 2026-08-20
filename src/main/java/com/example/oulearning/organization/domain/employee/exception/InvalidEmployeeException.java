package com.example.oulearning.organization.domain.employee.exception;

import com.example.oulearning.organization.domain.employee.EmployeeConstants;

public final class InvalidEmployeeException extends EmployeeException {

    public InvalidEmployeeException(final String message) {
        super(message);
    }

    public InvalidEmployeeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public static InvalidEmployeeException nullField(final String fieldName) {
        return new InvalidEmployeeException("%s cannot be null".formatted(fieldName));
    }

    public static InvalidEmployeeException blankField(final String fieldName) {
        return new InvalidEmployeeException("%s cannot be blank".formatted(fieldName));
    }

    public static InvalidEmployeeException nullOrBlank(final String fieldName) {
        return new InvalidEmployeeException("%s string cannot be null or blank".formatted(fieldName));
    }

    public static InvalidEmployeeException lengthOutOfRange(
            final String fieldName, final int min, final int max, final String actual) {
        return new InvalidEmployeeException(
                "%s length must be between %d and %d characters: %s".formatted(fieldName, min, max, actual));
    }

    public static InvalidEmployeeException lengthExceedsMax(
            final String fieldName, final int max, final String actual) {
        return new InvalidEmployeeException(
                "%s length exceeds maximum of %d characters: %s".formatted(fieldName, max, actual));
    }

    public static InvalidEmployeeException nonPositiveId(final String fieldName, final long value) {
        return new InvalidEmployeeException(
                "%s must be strictly positive (at least %d): %d".formatted(fieldName, EmployeeConstants.MIN_ID, value));
    }

    public static InvalidEmployeeException invalidId(
            final String fieldName, final String value, final Throwable cause) {
        return new InvalidEmployeeException("Invalid %s format: %s".formatted(fieldName, value), cause);
    }
}
