package com.example.oulearning.organization.domain.hierarchy;

/**
 * Value object representing an organizational unit name.
 *
 * @param value the non-blank name string
 */
public record Name(String value) {

    public Name {
        if (value == null) {
            throw new InvalidOuException("Name cannot be null");
        }
        value = value.strip();
        if (value.isBlank()) {
            throw new InvalidOuException("Name cannot be blank");
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
