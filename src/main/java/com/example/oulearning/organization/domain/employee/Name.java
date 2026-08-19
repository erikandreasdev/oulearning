package com.example.oulearning.organization.domain.employee;


public record Name(String value) {

    public Name {
        value = EmployeeGuard.requireLengthBetween(
                value, "Name", EmployeeConstants.MIN_NAME_LENGTH, EmployeeConstants.MAX_NAME_LENGTH);
    }

    public static Name of(final String value) {
        return new Name(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
