package com.example.oulearning.organization.domain.employee;

/**
 * Base sealed exception for all domain invariant violations in the employee domain.
 */
public abstract sealed class EmployeeException extends RuntimeException
        permits InvalidEmailException, InvalidEmployeeException {

    protected EmployeeException(String message) {
        super(message);
    }

    protected EmployeeException(String message, Throwable cause) {
        super(message, cause);
    }
}
