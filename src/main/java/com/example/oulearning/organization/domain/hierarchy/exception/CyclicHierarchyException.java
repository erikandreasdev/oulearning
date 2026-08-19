package com.example.oulearning.organization.domain.hierarchy.exception;

/** Exception thrown when a cyclic hierarchy is detected. */
public final class CyclicHierarchyException extends HierarchyException {

  public CyclicHierarchyException(String message) {
    super(message);
  }
}
