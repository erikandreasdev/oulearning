package com.example.oulearning.budgeting.domain;

/**
 * Base sealed exception for all domain invariant violations in the budgeting context.
 */
public abstract sealed class BudgetingException extends RuntimeException
        permits CurrencyMismatchException, InsufficientBudgetException, InvalidBudgetOperationException {

    protected BudgetingException(String message) {
        super(message);
    }

    protected BudgetingException(String message, Throwable cause) {
        super(message, cause);
    }
}
