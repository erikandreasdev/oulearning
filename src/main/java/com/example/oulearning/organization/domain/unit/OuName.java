package com.example.oulearning.organization.domain.unit;

import com.example.oulearning.organization.domain.unit.exception.InvalidOuException;
import java.util.regex.Pattern;

/**
 * Value object representing an Organizational Unit name.
 */
public record OuName(String value) {

    private static final int MIN_LENGTH = 1;
    private static final int MAX_LENGTH = 100;
    private static final Pattern PATTERN = Pattern.compile("^[\\p{L}0-9 _.'-]+$");

    public OuName {
        if (value == null || value.isBlank()) {
            throw new InvalidOuException("OuName cannot be null or blank");
        }
        final var trimmed = value.strip();
        if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH) {
            throw new InvalidOuException(
                    "OuName length must be between %d and %d characters (actual: %d)"
                            .formatted(MIN_LENGTH, MAX_LENGTH, trimmed.length()));
        }
        if (!PATTERN.matcher(trimmed).matches()) {
            throw new InvalidOuException(
                    "OuName contains invalid characters: '%s'. Allowed: letters, numbers, spaces, hyphens, dots, apostrophes, underscores"
                            .formatted(trimmed));
        }
        value = trimmed;
    }

    public static OuName of(String value) {
        return new OuName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
