package com.example.oulearning.organization.domain.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.exception.InvalidOrganizationalUnitException;
import java.util.UUID;

final class HierarchyGuard {

    private HierarchyGuard() {
    }

    static OrganizationalUnitId requireOrganizationalUnitId(final OrganizationalUnitId id) {
        return requireNonNull(id, "Organizational unit id");
    }

    static UUID requireOrganizationalUnitId(final UUID value) {
        return requireNonNull(value, "Organizational unit id");
    }

    static UUID requireValidOrganizationalUnitId(final String value) {
        return requireValidUuid(value, "Organizational unit id");
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

    private static UUID requireValidUuid(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw InvalidOrganizationalUnitException.nullOrBlank(fieldName);
        }
        try {
            return UUID.fromString(value.strip());
        } catch (final IllegalArgumentException e) {
            throw InvalidOrganizationalUnitException.invalidUuid(value, e);
        }
    }
}
