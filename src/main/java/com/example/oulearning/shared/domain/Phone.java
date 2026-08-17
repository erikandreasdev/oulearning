package com.example.oulearning.shared.domain;

import static com.example.oulearning.shared.domain.DomainPatterns.PHONE_PATTERN;

/**
 * Value object representing a telephone number.
 * <p>
 * Ensures the phone number is normalized (stripping formatting characters such as spaces, hyphens, dots,
 * and parentheses) and validated against E.164-compatible telephone format constraints (7 to 15 digits with optional
 * leading '+').
 * </p>
 *
 * @param value the normalized phone number string
 */
public record Phone(String value) {

    /**
     * Compact constructor enforcing phone invariants and normalization.
     */
    public Phone {
        if (value == null) {
            throw new InvalidPhoneException(null, "Phone cannot be null");
        }

        String trimmed = value.strip();
        if (trimmed.isBlank()) {
            throw new InvalidPhoneException(value, "Phone cannot be blank");
        }

        // Normalize by removing spaces, hyphens, dots, and parentheses
        value = trimmed.replaceAll("[\\s\\-\\(\\)\\.]", "");

        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new InvalidPhoneException(value, "Invalid phone number format: " + trimmed);
        }
    }

    /**
     * Factory method to create a {@link Phone}.
     *
     * @param value the raw phone number string
     * @return a validated {@link Phone} value object
     */
    public static Phone of(String value) {
        return new Phone(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
