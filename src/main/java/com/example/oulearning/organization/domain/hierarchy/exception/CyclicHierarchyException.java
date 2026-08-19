package com.example.oulearning.organization.domain.hierarchy.exception;


public final class CyclicHierarchyException extends HierarchyException {

  public CyclicHierarchyException(String message) {
    super(message);
  }
}
