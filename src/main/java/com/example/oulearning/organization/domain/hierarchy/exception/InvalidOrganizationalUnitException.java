package com.example.oulearning.organization.domain.hierarchy.exception;

import com.example.oulearning.organization.domain.hierarchy.model.HierarchyConstants;

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

    public static InvalidOrganizationalUnitException nonPositiveId(final String fieldName, final long value) {
        return new InvalidOrganizationalUnitException(
                "%s must be strictly positive (at least %d): %d".formatted(fieldName, HierarchyConstants.MIN_ID, value));
    }

    public static InvalidOrganizationalUnitException invalidId(
            final String fieldName, final String value, final Throwable cause) {
        return new InvalidOrganizationalUnitException("Invalid %s format: %s".formatted(fieldName, value), cause);
    }
}
