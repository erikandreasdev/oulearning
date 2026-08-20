package com.example.oulearning.budgeting.domain;


public record BudgetId(long value) {

    public BudgetId {
        BudgetingGuard.requirePositiveBudgetId(value);
    }

    public static BudgetId of(final long value) {
        return new BudgetId(value);
    }

    public static BudgetId fromString(final String value) {
        return new BudgetId(BudgetingGuard.requireValidBudgetId(value));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
