package com.example.oulearning.organization.domain;

import static com.example.oulearning.shared.domain.DomainPatterns.EMAIL_PATTERN;

/**
 * Value object representing an electronic mail address within the organization context.
 * <p>
 * Ensures the email is normalized (trimmed and converted to lowercase) and validated against standard email format
 * constraints.
 * </p>
 *
 * @param value the normalized email string
 */
public record Email(String value) {

    /**
     * Compact constructor enforcing email invariants.
     */
    public Email {
        if (value == null) {
            throw new InvalidEmailException(null, "Email cannot be null");
        }

        value = value.strip().toLowerCase();

        if (value.isBlank()) {
            throw new InvalidEmailException(value, "Email cannot be blank");
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidEmailException(value, "Invalid email format: %s".formatted(value));
        }
    }

    /**
     * Factory method to create an {@link Email}.
     *
     * @param value the raw email string
     * @return a validated {@link Email} value object
     */
    public static Email of(String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
