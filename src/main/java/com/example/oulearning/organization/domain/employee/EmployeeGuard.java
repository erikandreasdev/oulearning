package com.example.oulearning.organization.domain.employee;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmployeeException;
import java.util.UUID;

final class EmployeeGuard {

    private EmployeeGuard() {
    }

    static <T> T requireNonNull(final T value, final String fieldName) {
        if (value == null) {
            throw InvalidEmployeeException.nullField(fieldName);
        }
        return value;
    }

    static String requireNonBlank(final String value, final String fieldName) {
        final var notNull = requireNonNull(value, fieldName).strip();
        if (notNull.isBlank()) {
            throw InvalidEmployeeException.blankField(fieldName);
        }
        return notNull;
    }

    static String requireLengthBetween(
            final String value, final String fieldName, final int min, final int max) {
        final var stripped = requireNonBlank(value, fieldName);
        if (stripped.length() < min || stripped.length() > max) {
            throw InvalidEmployeeException.lengthOutOfRange(fieldName, min, max, stripped);
        }
        return stripped;
    }

    static UUID requireValidUuid(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw InvalidEmployeeException.nullOrBlank(fieldName);
        }
        try {
            return UUID.fromString(value.strip());
        } catch (final IllegalArgumentException e) {
            throw InvalidEmployeeException.invalidUuid(value, e);
        }
    }
}
