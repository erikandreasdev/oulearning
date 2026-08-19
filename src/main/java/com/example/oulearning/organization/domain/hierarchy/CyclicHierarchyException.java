package com.example.oulearning.organization.domain.hierarchy;

/**
 * Exception thrown when a hierarchy relationship would create a cycle or invalid self-reference.
 */
public final class CyclicHierarchyException extends HierarchyException {

    public CyclicHierarchyException(String message) {
        super(message);
    }
}
