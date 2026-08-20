package com.example.oulearning.organization.domain.employee;


public record Name(String value) {

    public Name {
        value = EmployeeGuard.requireName(value);
    }

    public static Name of(final String value) {
        return new Name(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
