package com.example.oulearning.budgeting.domain;

import com.example.oulearning.shared.domain.DomainException;

/**
 * Domain exception thrown when applying a budget distribution strategy fails.
 */
public final class BudgetDistributionException extends DomainException {

    public BudgetDistributionException(String message) {
        super(message);
    }
}
