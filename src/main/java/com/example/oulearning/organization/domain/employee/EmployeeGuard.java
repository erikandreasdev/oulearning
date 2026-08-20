package com.example.oulearning.organization.domain.employee;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmailException;
import com.example.oulearning.organization.domain.employee.exception.InvalidEmployeeException;
import java.util.UUID;
import java.util.regex.Pattern;

final class EmployeeGuard {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EmployeeConstants.EMAIL_REGEX);

    private EmployeeGuard() {
    }

    static EmployeeId requireEmployeeId(final EmployeeId id) {
        return requireNonNull(id, "Employee id");
    }

    static UUID requireEmployeeId(final UUID value) {
        return requireNonNull(value, "Employee id");
    }

    static UUID requireValidEmployeeId(final String value) {
        return requireValidUuid(value, "Employee id");
    }

    static FullName requireFullName(final FullName fullName) {
        return requireNonNull(fullName, "FullName");
    }

    static Name requireFirstName(final Name name) {
        return requireNonNull(name, "First name");
    }

    static Surname requireSurname(final Surname surname) {
        return requireNonNull(surname, "Surname");
    }

    static void requireValidFullName(final String formattedName) {
        if (formattedName.isBlank()) {
            throw InvalidEmployeeException.blankField("Full name");
        }
        if (formattedName.length() > EmployeeConstants.MAX_FULL_NAME_LENGTH) {
            throw InvalidEmployeeException.lengthExceedsMax(
                    "Full name", EmployeeConstants.MAX_FULL_NAME_LENGTH, formattedName);
        }
    }

    static String requireName(final String value) {
        return requireLengthBetween(
                value, "Name", EmployeeConstants.MIN_NAME_LENGTH, EmployeeConstants.MAX_NAME_LENGTH);
    }

    static String requireSurname(final String value) {
        return requireLengthBetween(
                value, "Surname", EmployeeConstants.MIN_SURNAME_LENGTH, EmployeeConstants.MAX_SURNAME_LENGTH);
    }

    static Email requireEmail(final Email email) {
        return requireNonNull(email, "Email");
    }

    static String requireValidEmail(final String value) {
        if (value == null) {
            throw InvalidEmailException.nullField();
        }
        final var stripped = value.strip().toLowerCase();
        if (stripped.isBlank()) {
            throw InvalidEmailException.blankField();
        }
        if (!EMAIL_PATTERN.matcher(stripped).matches()) {
            throw InvalidEmailException.invalidFormat(stripped);
        }
        return stripped;
    }

    private static <T> T requireNonNull(final T value, final String fieldName) {
        if (value == null) {
            throw InvalidEmployeeException.nullField(fieldName);
        }
        return value;
    }

    private static String requireNonBlank(final String value, final String fieldName) {
        final var notNull = requireNonNull(value, fieldName).strip();
        if (notNull.isBlank()) {
            throw InvalidEmployeeException.blankField(fieldName);
        }
        return notNull;
    }

    private static String requireLengthBetween(
            final String value, final String fieldName, final int min, final int max) {
        final var stripped = requireNonBlank(value, fieldName);
        if (stripped.length() < min || stripped.length() > max) {
            throw InvalidEmployeeException.lengthOutOfRange(fieldName, min, max, stripped);
        }
        return stripped;
    }

    private static UUID requireValidUuid(final String value, final String fieldName) {
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
