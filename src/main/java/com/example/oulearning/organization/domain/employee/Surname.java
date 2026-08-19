package com.example.oulearning.organization.domain.employee;

/**
 * Value object representing an employee's surname / last name.
 *
 * @param value the non-blank surname string
 */
public record Surname(String value) {

    public Surname {
        if (value == null) {
            throw new InvalidEmployeeException("Surname cannot be null");
        }
        value = value.strip();
        if (value.isBlank()) {
            throw new InvalidEmployeeException("Surname cannot be blank");
        }
    }

    public static Surname of(String value) {
        return new Surname(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
