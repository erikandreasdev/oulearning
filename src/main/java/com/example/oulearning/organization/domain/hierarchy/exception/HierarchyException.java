package com.example.oulearning.organization.domain.hierarchy.exception;


public abstract sealed class HierarchyException extends RuntimeException
    permits InvalidOuException, CyclicHierarchyException {

  protected HierarchyException(String message) {
    super(message);
  }

  protected HierarchyException(String message, Throwable cause) {
    super(message, cause);
  }
}
