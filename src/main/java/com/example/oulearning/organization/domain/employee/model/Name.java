package com.example.oulearning.organization.domain.employee.model;


public record Name(String value) {

    public Name {
        value = EmployeeGuard.requireValidName(value);
    }

    public static Name of(final String value) {
        return new Name(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
