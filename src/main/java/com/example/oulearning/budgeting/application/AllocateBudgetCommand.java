package com.example.oulearning.budgeting.application;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Immutable application command for allocating budget to an OU for a specific fiscal year.
 */
public record AllocateBudgetCommand(
        UUID budgetId,
        UUID ouId,
        Integer fiscalYear,
        BigDecimal amount,
        String currencyCode) {

    public AllocateBudgetCommand(UUID budgetId, UUID ouId, BigDecimal amount, String currencyCode) {
        this(budgetId, ouId, 2026, amount, currencyCode);
    }
}
