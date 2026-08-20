package com.example.oulearning.organization.domain.employee;


public record Surname(String value) {

    public Surname {
        value = EmployeeGuard.requireSurname(value);
    }

    public static Surname of(final String value) {
        return new Surname(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
