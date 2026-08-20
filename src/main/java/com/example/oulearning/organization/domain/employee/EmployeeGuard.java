package com.example.oulearning.organization.domain.employee;

import com.example.oulearning.organization.domain.employee.exception.InvalidEmailException;
import com.example.oulearning.organization.domain.employee.exception.InvalidEmployeeException;
import java.util.regex.Pattern;

final class EmployeeGuard {

    private static final String FIELD_EMPLOYEE_ID = "Employee id";
    private static final String FIELD_FULL_NAME = "Full name";
    private static final String FIELD_FIRST_NAME = "First name";
    private static final String FIELD_NAME = "Name";
    private static final String FIELD_SURNAME = "Surname";
    private static final String FIELD_EMAIL = "Email";
    private static final String FIELD_FULLNAME_VO = "FullName";

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(EmployeeConstants.EMAIL_REGEX);

    private EmployeeGuard() {
    }

    static void requireEmployeeId(final EmployeeId id) {
        requireNonNull(id, FIELD_EMPLOYEE_ID);
    }

    static void requirePositiveEmployeeId(final long value) {
        requirePositiveId(value, FIELD_EMPLOYEE_ID);
    }

    static long requireValidEmployeeId(final String value) {
        return requireValidId(value, FIELD_EMPLOYEE_ID);
    }

    static void requireFullName(final FullName fullName) {
        requireNonNull(fullName, FIELD_FULLNAME_VO);
    }

    static void requireValidFullName(final String formattedName) {
        if (formattedName.isBlank()) {
            throw InvalidEmployeeException.blankField(FIELD_FULL_NAME);
        }
        if (formattedName.length() > EmployeeConstants.MAX_FULL_NAME_LENGTH) {
            throw InvalidEmployeeException.lengthExceedsMax(
                    FIELD_FULL_NAME, EmployeeConstants.MAX_FULL_NAME_LENGTH, formattedName);
        }
    }

    static void requireFirstName(final Name name) {
        requireNonNull(name, FIELD_FIRST_NAME);
    }

    static String requireValidName(final String value) {
        return requireLengthBetween(
                value,
                FIELD_NAME,
                EmployeeConstants.MIN_NAME_LENGTH,
                EmployeeConstants.MAX_NAME_LENGTH);
    }

    static void requireSurname(final Surname surname) {
        requireNonNull(surname, FIELD_SURNAME);
    }

    static String requireValidSurname(final String value) {
        return requireLengthBetween(
                value,
                FIELD_SURNAME,
                EmployeeConstants.MIN_SURNAME_LENGTH,
                EmployeeConstants.MAX_SURNAME_LENGTH);
    }

    static void requireEmail(final Email email) {
        requireNonNull(email, FIELD_EMAIL);
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

    private static <T> void requireNonNull(final T value, final String fieldName) {
        if (value == null) {
            throw InvalidEmployeeException.nullField(fieldName);
        }
    }

    private static String requireNonBlank(final String value, final String fieldName) {
        requireNonNull(value, fieldName);
        final var notNull = value.strip();
        if (notNull.isBlank()) {
            throw InvalidEmployeeException.blankField(fieldName);
        }
        return notNull;
    }

    private static String requireLengthBetween(
            final String value, final String fieldName, final int min, final int max) {
        final var stripped = requireNonBlank(value, fieldName);
        if (stripped.length() < min || stripped.length() > max) {
            throw InvalidEmployeeException.lengthOutOfRange(
                    fieldName, min, max, stripped);
        }
        return stripped;
    }

    private static void requirePositiveId(final long value, final String fieldName) {
        if (value < EmployeeConstants.MIN_ID) {
            throw InvalidEmployeeException.nonPositiveId(fieldName, value);
        }
    }

    private static long requireValidId(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw InvalidEmployeeException.nullOrBlank(fieldName);
        }
        try {
            final var parsed = Long.parseLong(value.strip());
            requirePositiveId(parsed, fieldName);
            return parsed;
        } catch (final NumberFormatException e) {
            throw InvalidEmployeeException.invalidId(fieldName, value, e);
        }
    }
}
