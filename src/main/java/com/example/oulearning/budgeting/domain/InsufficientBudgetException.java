package com.example.oulearning.budgeting.domain;

import com.example.oulearning.shared.domain.DomainException;

/**
 * Domain exception thrown when an operation exceeds available budget funds.
 */
public final class InsufficientBudgetException extends DomainException {

    public InsufficientBudgetException(String message) {
        super(message);
    }
}
