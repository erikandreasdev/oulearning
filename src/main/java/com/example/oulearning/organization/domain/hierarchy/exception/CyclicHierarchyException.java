package com.example.oulearning.organization.domain.hierarchy.exception;

public final class CyclicHierarchyException extends HierarchyException {

    public CyclicHierarchyException(final String message) {
        super(message);
    }

    public CyclicHierarchyException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
