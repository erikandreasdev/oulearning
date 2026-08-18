package com.example.oulearning.budgeting.application.port.in.query;

import java.util.UUID;

/**
 * Immutable application query for retrieving a Budget.
 */
public record GetBudgetQuery(
        UUID budgetId,
        UUID ouId,
        Integer fiscalYear) {

    public static GetBudgetQuery byBudgetId(UUID budgetId) {
        return new GetBudgetQuery(budgetId, null, null);
    }

    public static GetBudgetQuery byOuId(UUID ouId) {
        return new GetBudgetQuery(null, ouId, null);
    }

    public static GetBudgetQuery byOuIdAndFiscalYear(UUID ouId, int fiscalYear) {
        return new GetBudgetQuery(null, ouId, fiscalYear);
    }
}
