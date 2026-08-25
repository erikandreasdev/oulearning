package com.example.oulearning.organization.domain.employee.model;


public record Email(String value) {

    public Email {
        value = EmployeeGuard.requireValidEmail(value);
    }

    public static Email of(final String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
