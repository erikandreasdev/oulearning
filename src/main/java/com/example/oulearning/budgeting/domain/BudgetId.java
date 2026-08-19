package com.example.oulearning.budgeting.domain;

import java.util.UUID;


public record BudgetId(UUID value) {

    public BudgetId {
        BudgetingGuard.requireNonNull(value, "Budget id");
    }

    public static BudgetId of(final UUID value) {
        return new BudgetId(value);
    }

    public static BudgetId fromString(final String value) {
        return new BudgetId(BudgetingGuard.requireValidUuid(value, "Budget id"));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
