package com.example.oulearning.organization.domain.employee.vo.name;

import com.example.oulearning.organization.domain.employee.exception.name.InvalidFullNameException;

/**
 * Composite value object representing a person's complete name.
 */
public record FullName(Name name, Surname surname) {

    public FullName {
        if (name == null) {
            throw new InvalidFullNameException("Name cannot be null in FullName");
        }
        if (surname == null) {
            throw new InvalidFullNameException("Surname cannot be null in FullName");
        }
    }

    public static FullName of(Name name, Surname surname) {
        return new FullName(name, surname);
    }

    public static FullName of(String firstName, String lastName) {
        return new FullName(Name.of(firstName), Surname.of(lastName));
    }

    public String formatted() {
        return "%s %s".formatted(name.value(), surname.value());
    }

    @Override
    public String toString() {
        return formatted();
    }
}
