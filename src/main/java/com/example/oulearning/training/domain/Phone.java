package com.example.oulearning.training.domain;

import java.util.regex.Pattern;

/**
 * Value object representing an external provider's telephone number.
 *
 * @param value the normalized phone number string
 */
public record Phone(String value) {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{6,14}$");

    public Phone {
        if (value == null) {
            throw new InvalidTrainingOperationException("Phone cannot be null");
        }

        String trimmed = value.strip();
        if (trimmed.isBlank()) {
            throw new InvalidTrainingOperationException("Phone cannot be blank");
        }

        // Normalize by removing spaces, hyphens, dots, and parentheses
        value = trimmed.replaceAll("[\\s\\-\\(\\)\\.]", "");

        if (!PHONE_PATTERN.matcher(value).matches()) {
            throw new InvalidTrainingOperationException("Invalid phone number format: " + trimmed);
        }
    }

    public static Phone of(String value) {
        return new Phone(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
