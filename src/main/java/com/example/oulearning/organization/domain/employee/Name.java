package com.example.oulearning.organization.domain.employee;

/**
 * Value object representing an employee's first/given name.
 *
 * @param value the non-blank name string
 */
public record Name(String value) {

    public Name {
        if (value == null) {
            throw new InvalidEmployeeException("Name cannot be null");
        }
        value = value.strip();
        if (value.isBlank()) {
            throw new InvalidEmployeeException("Name cannot be blank");
        }
    }

    public static Name of(String value) {
        return new Name(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
