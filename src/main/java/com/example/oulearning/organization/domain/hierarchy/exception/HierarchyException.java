package com.example.oulearning.organization.domain.hierarchy.exception;

/** Base sealed exception for all domain errors within the hierarchy bounded context. */
public abstract sealed class HierarchyException extends RuntimeException
    permits InvalidOuException, CyclicHierarchyException {

  protected HierarchyException(String message) {
    super(message);
  }

  protected HierarchyException(String message, Throwable cause) {
    super(message, cause);
  }
}
