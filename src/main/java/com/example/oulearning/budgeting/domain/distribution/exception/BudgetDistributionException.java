package com.example.oulearning.budgeting.domain.distribution.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Domain exception thrown when applying a budget distribution strategy fails.
 */
public final class BudgetDistributionException extends DomainException {

    public BudgetDistributionException(String message) {
        super(message);
    }
}
