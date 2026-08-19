package com.example.oulearning.budgeting.domain.exception;


public final class InsufficientBudgetException extends BudgetingException {

  public InsufficientBudgetException(String message) {
    super(message);
  }
}
