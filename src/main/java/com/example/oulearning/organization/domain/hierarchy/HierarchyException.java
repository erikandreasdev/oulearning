package com.example.oulearning.organization.domain.hierarchy;

/**
 * Base sealed exception for all domain invariant violations in organizational hierarchy.
 */
public abstract sealed class HierarchyException extends RuntimeException
        permits InvalidOuException, CyclicHierarchyException {

    protected HierarchyException(String message) {
        super(message);
    }

    protected HierarchyException(String message, Throwable cause) {
        super(message, cause);
    }
}
