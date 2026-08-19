package com.example.oulearning.budgeting.domain;


public record FiscalYear(int value) {

    public FiscalYear {
        value = BudgetingGuard.requireFiscalYearBetween(
                value, BudgetingConstants.MIN_FISCAL_YEAR, BudgetingConstants.MAX_FISCAL_YEAR);
    }

    public static FiscalYear of(final int value) {
        return new FiscalYear(value);
    }

    @Override
    public String toString() {
        return "%d".formatted(value);
    }
}
