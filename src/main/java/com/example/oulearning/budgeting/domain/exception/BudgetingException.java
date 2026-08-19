package com.example.oulearning.budgeting.domain.exception;


public abstract sealed class BudgetingException extends RuntimeException
        permits InsufficientBudgetException, InvalidBudgetOperationException {

    protected BudgetingException(String message) {
        super(message);
    }

    protected BudgetingException(String message, Throwable cause) {
        super(message, cause);
    }
}
