package com.example.oulearning.organization.domain.employee.model;


public record Surname(String value) {

    public Surname {
        value = EmployeeGuard.requireValidSurname(value);
    }

    public static Surname of(final String value) {
        return new Surname(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
