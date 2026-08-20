package com.example.oulearning.organization.domain.hierarchy.exception;


public final class InvalidOrganizationalUnitException extends HierarchyException {

    public InvalidOrganizationalUnitException(final String message) {
        super(message);
    }

    public InvalidOrganizationalUnitException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public static InvalidOrganizationalUnitException nullField(final String fieldName) {
        return new InvalidOrganizationalUnitException("%s cannot be null".formatted(fieldName));
    }

    public static InvalidOrganizationalUnitException blankField(final String fieldName) {
        return new InvalidOrganizationalUnitException("%s cannot be blank".formatted(fieldName));
    }

    public static InvalidOrganizationalUnitException nullOrBlank(final String fieldName) {
        return new InvalidOrganizationalUnitException("%s string cannot be null or blank".formatted(fieldName));
    }

    public static InvalidOrganizationalUnitException lengthOutOfRange(
            final String fieldName, final int min, final int max, final String actual) {
        return new InvalidOrganizationalUnitException(
                "%s length must be between %d and %d characters: %s".formatted(fieldName, min, max, actual));
    }

    public static InvalidOrganizationalUnitException invalidUuid(final String value) {
        return new InvalidOrganizationalUnitException("Invalid UUID format: %s".formatted(value));
    }

    public static InvalidOrganizationalUnitException invalidUuid(final String value, final Throwable cause) {
        return new InvalidOrganizationalUnitException("Invalid UUID format: %s".formatted(value), cause);
    }
}
