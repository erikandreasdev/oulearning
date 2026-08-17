package com.example.oulearning.organization.domain.employee;

import com.example.oulearning.organization.domain.employee.exception.InvalidPhoneException;
import java.util.regex.Pattern;

/**
 * Value object representing an E.164-compatible telephone number.
 */
public record Phone(String value) {

    private static final Pattern STRIP_PATTERN = Pattern.compile("[\\s\\-().]");
    private static final Pattern VALID_PHONE_PATTERN = Pattern.compile("^\\+?[1-9]\\d{6,14}$");

    public Phone {
        if (value == null || value.isBlank()) {
            throw new InvalidPhoneException("Phone cannot be null or blank");
        }
        final var stripped = STRIP_PATTERN.matcher(value.strip()).replaceAll("");
        if (!VALID_PHONE_PATTERN.matcher(stripped).matches()) {
            throw new InvalidPhoneException("Invalid phone number format: '%s'".formatted(value));
        }
        value = stripped;
    }

    public static Phone of(String value) {
        return new Phone(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
