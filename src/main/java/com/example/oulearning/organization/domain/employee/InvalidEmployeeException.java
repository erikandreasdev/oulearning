package com.example.oulearning.organization.domain.employee;

/**
 * Domain exception thrown when employee validation rules or invariants are violated.
 */
public final class InvalidEmployeeException extends EmployeeException {

    public InvalidEmployeeException(String message) {
        super(message);
    }
}
