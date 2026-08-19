package com.example.oulearning.budgeting.domain;

/**
 * Exception thrown when monetary operations are attempted on differing currencies.
 */
public final class CurrencyMismatchException extends BudgetingException {

    public CurrencyMismatchException(String message) {
        super(message);
    }
}
