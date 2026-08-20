package com.example.oulearning.organization.domain.hierarchy.exception;


public abstract sealed class HierarchyException extends RuntimeException
        permits InvalidOrganizationalUnitException, CyclicHierarchyException {

    protected HierarchyException(final String message) {
        super(message);
    }

    protected HierarchyException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
