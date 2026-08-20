package com.example.oulearning.organization.domain.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.exception.InvalidOrganizationalUnitException;

final class HierarchyGuard {

    private HierarchyGuard() {
    }

    static OrganizationalUnitId requireOrganizationalUnitId(final OrganizationalUnitId id) {
        return requireNonNull(id, "Organizational unit id");
    }

    static long requireOrganizationalUnitId(final long value) {
        return requirePositiveId(value, "Organizational unit id");
    }

    static long requireValidOrganizationalUnitId(final String value) {
        return requireValidId(value, "Organizational unit id");
    }

    static Name requireName(final Name name) {
        return requireNonNull(name, "Name");
    }

    static String requireOrganizationalUnitName(final String value) {
        return requireLengthBetween(
                value,
                "Organizational unit name",
                HierarchyConstants.MIN_NAME_LENGTH,
                HierarchyConstants.MAX_NAME_LENGTH);
    }

    private static <T> T requireNonNull(final T value, final String fieldName) {
        if (value == null) {
            throw InvalidOrganizationalUnitException.nullField(fieldName);
        }
        return value;
    }

    private static String requireNonBlank(final String value, final String fieldName) {
        final var notNull = requireNonNull(value, fieldName).strip();
        if (notNull.isBlank()) {
            throw InvalidOrganizationalUnitException.blankField(fieldName);
        }
        return notNull;
    }

    private static String requireLengthBetween(
            final String value, final String fieldName, final int min, final int max) {
        final var stripped = requireNonBlank(value, fieldName);
        if (stripped.length() < min || stripped.length() > max) {
            throw InvalidOrganizationalUnitException.lengthOutOfRange(fieldName, min, max, stripped);
        }
        return stripped;
    }

    private static long requirePositiveId(final long value, final String fieldName) {
        if (value < HierarchyConstants.MIN_ID) {
            throw InvalidOrganizationalUnitException.nonPositiveId(fieldName, value);
        }
        return value;
    }

    private static long requireValidId(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw InvalidOrganizationalUnitException.nullOrBlank(fieldName);
        }
        try {
            final var parsed = Long.parseLong(value.strip());
            return requirePositiveId(parsed, fieldName);
        } catch (final NumberFormatException e) {
            throw InvalidOrganizationalUnitException.invalidId(fieldName, value, e);
        }
    }
}
