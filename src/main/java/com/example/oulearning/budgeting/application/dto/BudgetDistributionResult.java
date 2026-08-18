package com.example.oulearning.budgeting.application.dto;

import com.example.oulearning.budgeting.domain.budget.Budget;
import java.util.List;

/**
 * Application result record representing the outcome of a budget distribution operation.
 */
public record BudgetDistributionResult(
        Budget parentBudget,
        List<Budget> childBudgets) {

    public BudgetDistributionResult {
        childBudgets = childBudgets != null ? List.copyOf(childBudgets) : List.of();
    }
}
