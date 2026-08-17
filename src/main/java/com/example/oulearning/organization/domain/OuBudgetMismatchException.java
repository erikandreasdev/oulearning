package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.DomainException;

/**
 * Domain exception thrown when the sum of an Organizational Unit's loaded child budgets does not equal its total budget.
 */
public final class OuBudgetMismatchException extends DomainException {

    public OuBudgetMismatchException(String message) {
        super(message);
    }
}
