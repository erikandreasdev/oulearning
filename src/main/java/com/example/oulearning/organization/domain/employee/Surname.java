package com.example.oulearning.organization.domain.employee;


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
