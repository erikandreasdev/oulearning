package com.example.oulearning.budgeting.domain.budget.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Domain exception thrown when budget invariant rules are violated.
 */
public final class InvalidBudgetException extends DomainException {

    public InvalidBudgetException(String message) {
        super(message);
    }
}
