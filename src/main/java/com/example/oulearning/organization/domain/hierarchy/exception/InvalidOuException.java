package com.example.oulearning.organization.domain.hierarchy.exception;


public final class InvalidOuException extends HierarchyException {

    public InvalidOuException(final String message) {
        super(message);
    }

    public static InvalidOuException nullField(final String fieldName) {
        return new InvalidOuException("%s cannot be null".formatted(fieldName));
    }

    public static InvalidOuException blankField(final String fieldName) {
        return new InvalidOuException("%s cannot be blank".formatted(fieldName));
    }

    public static InvalidOuException nullOrBlank(final String fieldName) {
        return new InvalidOuException("%s string cannot be null or blank".formatted(fieldName));
    }

    public static InvalidOuException lengthOutOfRange(
            final String fieldName, final int min, final int max, final String actual) {
        return new InvalidOuException(
                "%s length must be between %d and %d characters: %s".formatted(fieldName, min, max, actual));
    }

    public static InvalidOuException invalidUuid(final String value) {
        return new InvalidOuException("Invalid UUID format: %s".formatted(value));
    }
}
