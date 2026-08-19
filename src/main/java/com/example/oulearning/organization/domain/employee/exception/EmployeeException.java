package com.example.oulearning.organization.domain.employee.exception;


public abstract sealed class EmployeeException extends RuntimeException
    permits InvalidEmailException, InvalidEmployeeException {

  protected EmployeeException(String message) {
    super(message);
  }

  protected EmployeeException(String message, Throwable cause) {
    super(message, cause);
  }
}
