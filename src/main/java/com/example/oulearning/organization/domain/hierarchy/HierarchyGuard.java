package com.example.oulearning.organization.domain.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.exception.InvalidOuException;
import java.util.UUID;

final class HierarchyGuard {

    private HierarchyGuard() {}

    static <T> T requireNonNull(final T value, final String fieldName) {
        if (value == null) {
            throw InvalidOuException.nullField(fieldName);
        }
        return value;
    }

    static String requireNonBlank(final String value, final String fieldName) {
        final var notNull = requireNonNull(value, fieldName).strip();
        if (notNull.isBlank()) {
            throw InvalidOuException.blankField(fieldName);
        }
        return notNull;
    }

    static String requireLengthBetween(
            final String value, final String fieldName, final int min, final int max) {
        final var stripped = requireNonBlank(value, fieldName);
        if (stripped.length() < min || stripped.length() > max) {
            throw InvalidOuException.lengthOutOfRange(fieldName, min, max, stripped);
        }
        return stripped;
    }

    static UUID requireValidUuid(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw InvalidOuException.nullOrBlank(fieldName);
        }
        try {
            return UUID.fromString(value.strip());
        } catch (final IllegalArgumentException e) {
            throw InvalidOuException.invalidUuid(value);
        }
    }
}
