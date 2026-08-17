package com.example.oulearning.budgeting.domain;

import com.example.oulearning.shared.domain.DomainException;

/**
 * Domain exception thrown when budget invariant rules are violated.
 */
public final class InvalidBudgetException extends DomainException {

    public InvalidBudgetException(String message) {
        super(message);
    }
}
