package com.example.oulearning.organization.domain.employee;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmployeeException;

/**
 * Value object representing an employee's full name.
 *
 * @param name the employee's first name
 * @param surname the employee's surname
 */
public record FullName(Name name, Surname surname) {

    public FullName {
        if (name == null) {
            throw InvalidEmployeeException.nullField("First name");
        }
        if (surname == null) {
            throw InvalidEmployeeException.nullField("Surname");
        }
        final var formattedName = "%s %s".formatted(name.value(), surname.value());
        if (formattedName.isBlank()) {
            throw InvalidEmployeeException.blankField("Full name");
        }
        if (formattedName.length() > EmployeeConstants.MAX_FULL_NAME_LENGTH) {
            throw InvalidEmployeeException.lengthExceedsMax(
                    "Full name", EmployeeConstants.MAX_FULL_NAME_LENGTH, formattedName);
        }
    }

    public static FullName of(final Name name, final Surname surname) {
        return new FullName(name, surname);
    }

    public static FullName of(final String name, final String surname) {
        return new FullName(Name.of(name), Surname.of(surname));
    }

    public String formatted() {
        return "%s %s".formatted(name.value(), surname.value());
    }

    @Override
    public String toString() {
        return formatted();
    }
}
