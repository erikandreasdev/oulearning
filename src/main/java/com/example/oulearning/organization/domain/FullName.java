package com.example.oulearning.organization.domain;

/**
 * Composite value object representing a person's complete name within the organization context.
 *
 * @param name    the given/first name
 * @param surname the family/last name
 */
public record FullName(Name name, Surname surname) {

    /**
     * Compact constructor enforcing non-null name and surname.
     */
    public FullName {
        if (name == null) {
            throw new InvalidNameException(null, "Name in FullName cannot be null");
        }
        if (surname == null) {
            throw new InvalidSurnameException(null, "Surname in FullName cannot be null");
        }
    }

    /**
     * Factory method creating a {@link FullName} from typed value objects.
     *
     * @param name    the {@link Name} value object
     * @param surname the {@link Surname} value object
     * @return a {@link FullName} composite value object
     */
    public static FullName of(Name name, Surname surname) {
        return new FullName(name, surname);
    }

    /**
     * Factory method creating a {@link FullName} from raw strings.
     *
     * @param name    the raw first name string
     * @param surname the raw surname string
     * @return a validated {@link FullName} composite value object
     */
    public static FullName of(String name, String surname) {
        return new FullName(Name.of(name), Surname.of(surname));
    }

    /**
     * Returns the formatted full name as {@code "Name Surname"}.
     *
     * @return formatted full name string
     */
    public String formatted() {
        return "%s %s".formatted(name.value(), surname.value());
    }

    @Override
    public String toString() {
        return formatted();
    }
}
