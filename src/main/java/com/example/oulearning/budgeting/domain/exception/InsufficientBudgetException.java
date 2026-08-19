package com.example.oulearning.budgeting.domain.exception;

/** Exception thrown when attempting to reserve or spend more budget than available. */
public final class InsufficientBudgetException extends BudgetingException {

  public InsufficientBudgetException(String message) {
    super(message);
  }
}
