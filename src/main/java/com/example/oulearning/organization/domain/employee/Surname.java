package com.example.oulearning.organization.domain.employee;

/**
 * Value object representing an employee's surname.
 *
 * @param value the surname string
 */
public record Surname(String value) {

    public Surname {
        value = EmployeeGuard.requireLengthBetween(
                value, "Surname", EmployeeConstants.MIN_SURNAME_LENGTH, EmployeeConstants.MAX_SURNAME_LENGTH);
    }

    public static Surname of(final String value) {
        return new Surname(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
