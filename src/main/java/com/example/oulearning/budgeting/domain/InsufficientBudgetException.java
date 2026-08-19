package com.example.oulearning.budgeting.domain;

/**
 * Exception thrown when there is insufficient budget available for an operation.
 */
public final class InsufficientBudgetException extends BudgetingException {

    public InsufficientBudgetException(String message) {
        super(message);
    }
}
