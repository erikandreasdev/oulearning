package com.example.oulearning.organization.domain.employee.vo.name;

import com.example.oulearning.organization.domain.employee.exception.name.InvalidNameException;
import java.util.regex.Pattern;

/**
 * Value object representing a person's first/given name.
 */
public record Name(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;
    private static final Pattern PATTERN = Pattern.compile("^[\\p{L} .'-]+$");

    public Name {
        if (value == null || value.isBlank()) {
            throw new InvalidNameException("Name cannot be null or blank");
        }
        final var trimmed = value.strip();
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw new InvalidNameException(
                    "Name length must be between %d and %d characters (actual: %d)"
                            .formatted(MIN_LENGTH, MAX_LENGTH, trimmed.length()));
        }
        if (!PATTERN.matcher(trimmed).matches()) {
            throw new InvalidNameException(
                    "Name contains invalid characters: '%s'. Allowed: letters, spaces, hyphens, dots, apostrophes"
                            .formatted(trimmed));
        }
        value = trimmed;
    }

    public static Name of(String value) {
        return new Name(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
