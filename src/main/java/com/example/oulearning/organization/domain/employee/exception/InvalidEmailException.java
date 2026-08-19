package com.example.oulearning.organization.domain.employee.exception;

/** Exception thrown when an email address violates format constraints. */
public final class InvalidEmailException extends EmployeeException {

    public InvalidEmailException(final String message) {
        super(message);
    }

    public static InvalidEmailException nullField() {
        return new InvalidEmailException("Email cannot be null");
    }

    public static InvalidEmailException blankField() {
        return new InvalidEmailException("Email cannot be blank");
    }

    public static InvalidEmailException invalidFormat(final String email) {
        return new InvalidEmailException("Invalid email format: %s".formatted(email));
    }
}
