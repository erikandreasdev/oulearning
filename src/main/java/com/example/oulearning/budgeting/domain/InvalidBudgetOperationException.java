package com.example.oulearning.budgeting.domain;

/**
 * Exception thrown when a budget invariant or parameter is invalid.
 */
public final class InvalidBudgetOperationException extends BudgetingException {

    public InvalidBudgetOperationException(String message) {
        super(message);
    }
}
