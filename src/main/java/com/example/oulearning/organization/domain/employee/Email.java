package com.example.oulearning.organization.domain.employee;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmailException;
import java.util.regex.Pattern;


public record Email(String value) {

    private static final Pattern PATTERN = Pattern.compile(EmployeeConstants.EMAIL_REGEX);

    public Email {
        if (value == null) {
            throw InvalidEmailException.nullField();
        }
        final var stripped = value.strip().toLowerCase();
        if (stripped.isBlank()) {
            throw InvalidEmailException.blankField();
        }
        if (!PATTERN.matcher(stripped).matches()) {
            throw InvalidEmailException.invalidFormat(stripped);
        }
        value = stripped;
    }

    public static Email of(final String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
