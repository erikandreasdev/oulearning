package com.example.oulearning.budgeting.domain.budget.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Domain exception thrown when an operation exceeds available budget funds.
 */
public final class InsufficientBudgetException extends DomainException {

    public InsufficientBudgetException(String message) {
        super(message);
    }
}
