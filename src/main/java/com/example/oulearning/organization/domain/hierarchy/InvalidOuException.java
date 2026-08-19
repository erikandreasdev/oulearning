package com.example.oulearning.organization.domain.hierarchy;

/**
 * Exception thrown when an organizational unit is invalid or violates invariants.
 */
public final class InvalidOuException extends HierarchyException {

    public InvalidOuException(String message) {
        super(message);
    }
}
