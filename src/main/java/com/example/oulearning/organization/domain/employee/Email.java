package com.example.oulearning.organization.domain.employee;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmailException;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Value object representing an Email address.
 */
public record Email(String value) {

    private static final Pattern PATTERN =
            Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    public Email {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException("Email cannot be null or blank");
        }
        final var normalized = value.strip().toLowerCase(Locale.ROOT);
        if (!PATTERN.matcher(normalized).matches()) {
            throw new InvalidEmailException("Invalid email format: '%s'".formatted(value));
        }
        value = normalized;
    }

    public static Email of(String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
