package com.example.oulearning.organization.domain.employee;

/**
 * Value object representing an employee identifier (e.g. Workday ID, Corporate Key).
 *
 * @param value the non-blank employee identifier string
 */
public record Id(String value) {

    public Id {
        if (value == null) {
            throw new InvalidEmployeeException("Employee id cannot be null");
        }
        value = value.strip();
        if (value.isBlank()) {
            throw new InvalidEmployeeException("Employee id cannot be blank");
        }
    }

    public static Id of(String value) {
        return new Id(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
