package com.example.oulearning.budgeting.domain.exception;

/**
 * Base sealed exception for all domain errors within the budgeting bounded context.
 */
public abstract sealed class BudgetingException extends RuntimeException
        permits InsufficientBudgetException, InvalidBudgetOperationException {

    protected BudgetingException(String message) {
        super(message);
    }

    protected BudgetingException(String message, Throwable cause) {
        super(message, cause);
    }
}
