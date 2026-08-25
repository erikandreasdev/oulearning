package com.example.oulearning.budgeting.application.exception;

import com.example.oulearning.budgeting.domain.model.BudgetId;

public class BudgetNotFoundException extends RuntimeException {

    private final transient BudgetId budgetId;

    public BudgetNotFoundException(final BudgetId budgetId) {
        super("Budget not found with id: %s".formatted(budgetId));
        this.budgetId = budgetId;
    }

    public BudgetId budgetId() {
        return budgetId;
    }
}
