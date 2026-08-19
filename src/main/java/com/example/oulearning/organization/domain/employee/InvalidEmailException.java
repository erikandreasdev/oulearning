package com.example.oulearning.organization.domain.employee;

/**
 * Domain exception thrown when an email value does not meet domain validation rules.
 */
public final class InvalidEmailException extends EmployeeException {

    private final String invalidValue;

    public InvalidEmailException(String invalidValue, String message) {
        super(message);
        this.invalidValue = invalidValue;
    }

    public String getInvalidValue() {
        return invalidValue;
    }
}
