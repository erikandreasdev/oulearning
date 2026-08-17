package com.example.oulearning.budgeting.domain.budget.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Exception thrown when attempting to modify a budget belonging to an expired fiscal year.
 */
public class BudgetFiscalYearExpiredException extends DomainException {

    public BudgetFiscalYearExpiredException(String message) {
        super(message);
    }
}
