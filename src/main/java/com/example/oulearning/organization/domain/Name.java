package com.example.oulearning.organization.domain;

import static com.example.oulearning.shared.domain.DomainPatterns.NAME_PATTERN;

/**
 * Value object representing a person's given/first name within the organization context.
 * <p>
 * Ensures the name is normalized (trimmed and internal whitespace collapsed) and validated against format and length
 * constraints (1 to 100 characters, Unicode letters, spaces, hyphens, and apostrophes).
 * </p>
 *
 * @param value the normalized name string
 */
public record Name(String value) {

    public static final int MAX_LENGTH = 100;

    /**
     * Compact constructor enforcing name invariants and normalization.
     */
    public Name {
        if (value == null) {
            throw new InvalidNameException(null, "Name cannot be null");
        }

        String trimmed = value.strip();
        if (trimmed.isBlank()) {
            throw new InvalidNameException(value, "Name cannot be blank");
        }

        value = trimmed.replaceAll("\\s+", " ");

        if (value.length() > MAX_LENGTH) {
            throw new InvalidNameException(
                    value, "Name cannot exceed %d characters: %d".formatted(MAX_LENGTH, value.length()));
        }

        if (!NAME_PATTERN.matcher(value).matches()) {
            throw new InvalidNameException(value, "Invalid name format: %s".formatted(value));
        }
    }

    /**
     * Factory method to create a {@link Name}.
     *
     * @param value the raw name string
     * @return a validated {@link Name} value object
     */
    public static Name of(String value) {
        return new Name(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
