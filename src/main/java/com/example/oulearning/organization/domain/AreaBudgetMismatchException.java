package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.DomainException;

/**
 * Domain exception thrown when the sum of an Area's Subarea budgets does not equal the Area's total budget.
 */
public final class AreaBudgetMismatchException extends DomainException {

    public AreaBudgetMismatchException(String message) {
        super(message);
    }
}
