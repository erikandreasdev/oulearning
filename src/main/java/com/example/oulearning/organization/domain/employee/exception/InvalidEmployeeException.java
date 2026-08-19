package com.example.oulearning.organization.domain.employee.exception;

/** Exception thrown when an employee or employee identifier violates domain invariants. */
public final class InvalidEmployeeException extends EmployeeException {

    public InvalidEmployeeException(final String message) {
        super(message);
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

    public static InvalidEmployeeException invalidUuid(final String value) {
        return new InvalidEmployeeException("Invalid UUID format: %s".formatted(value));
    }
}
