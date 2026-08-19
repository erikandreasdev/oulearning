package com.example.oulearning.budgeting.domain;

/**
 * Value object representing a fiscal year (e.g., 2026).
 *
 * @param value the 4-digit fiscal year
 */
public record FiscalYear(int value) {

    public FiscalYear {
        if (value < 1900 || value > 3000) {
            throw new InvalidBudgetOperationException("Fiscal year must be between 1900 and 3000: " + value);
        }
    }

    public static FiscalYear of(int value) {
        return new FiscalYear(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
