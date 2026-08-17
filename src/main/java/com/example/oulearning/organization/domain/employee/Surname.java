package com.example.oulearning.organization.domain.employee;

import com.example.oulearning.organization.domain.employee.exception.InvalidSurnameException;
import java.util.regex.Pattern;

/**
 * Value object representing a person's last/family name (surname).
 */
public record Surname(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;
    private static final Pattern PATTERN = Pattern.compile("^[\\p{L} .'-]+$");

    public Surname {
        if (value == null || value.isBlank()) {
            throw new InvalidSurnameException("Surname cannot be null or blank");
        }
        final var trimmed = value.strip();
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw new InvalidSurnameException(
                    "Surname length must be between %d and %d characters (actual: %d)"
                            .formatted(MIN_LENGTH, MAX_LENGTH, trimmed.length()));
        }
        if (!PATTERN.matcher(trimmed).matches()) {
            throw new InvalidSurnameException(
                    "Surname contains invalid characters: '%s'. Allowed: letters, spaces, hyphens, dots, apostrophes"
                            .formatted(trimmed));
        }
        value = trimmed;
    }

    public static Surname of(String value) {
        return new Surname(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
