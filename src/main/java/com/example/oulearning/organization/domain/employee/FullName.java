package com.example.oulearning.organization.domain.employee;

/**
 * Value object representing an employee's full name composed of {@link Name} and {@link Surname}.
 *
 * @param name the first/given name
 * @param surname the last name / surname
 */
public record FullName(Name name, Surname surname) {

    public FullName {
        if (name == null) {
            throw new InvalidEmployeeException("Name cannot be null");
        }
        if (surname == null) {
            throw new InvalidEmployeeException("Surname cannot be null");
        }
    }

    public static FullName of(Name name, Surname surname) {
        return new FullName(name, surname);
    }

    public static FullName of(String name, String surname) {
        return new FullName(new Name(name), new Surname(surname));
    }

    public String formatted() {
        return name.value() + " " + surname.value();
    }

    @Override
    public String toString() {
        return formatted();
    }
}
