package com.example.oulearning.organization.domain;

import static com.example.oulearning.shared.domain.DomainPatterns.NAME_PATTERN;

/**
 * Value object representing a person's family/last name within the organization context.
 * <p>
 * Ensures the surname is normalized (trimmed and internal whitespace collapsed) and validated against format and length
 * constraints (1 to 100 characters, Unicode letters, spaces, hyphens, and apostrophes).
 * </p>
 *
 * @param value the normalized surname string
 */
public record Surname(String value) {

    public static final int MAX_LENGTH = 100;

    /**
     * Compact constructor enforcing surname invariants and normalization.
     */
    public Surname {
        if (value == null) {
            throw new InvalidSurnameException(null, "Surname cannot be null");
        }

        String trimmed = value.strip();
        if (trimmed.isBlank()) {
            throw new InvalidSurnameException(value, "Surname cannot be blank");
        }

        value = trimmed.replaceAll("\\s+", " ");

        if (value.length() > MAX_LENGTH) {
            throw new InvalidSurnameException(
                    value, "Surname cannot exceed %d characters: %d".formatted(MAX_LENGTH, value.length()));
        }

        if (!NAME_PATTERN.matcher(value).matches()) {
            throw new InvalidSurnameException(value, "Invalid surname format: %s".formatted(value));
        }
    }

    /**
     * Factory method to create a {@link Surname}.
     *
     * @param value the raw surname string
     * @return a validated {@link Surname} value object
     */
    public static Surname of(String value) {
        return new Surname(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
