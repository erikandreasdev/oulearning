package com.example.oulearning.organization.domain.employee;


public record FullName(Name name, Surname surname) {

    public FullName {
        EmployeeGuard.requireFirstName(name);
        EmployeeGuard.requireSurname(surname);
        final var formattedName = "%s %s".formatted(name.value(), surname.value());
        EmployeeGuard.requireValidFullName(formattedName);
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
