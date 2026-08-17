package com.example.oulearning.budgeting.infrastructure.persistence;

import java.math.BigDecimal;

/**
 * Persistence entity representing a row in the BUDGETS table.
 */
public record BudgetEntity(
        String id,
        String ouId,
        Integer fiscalYear,
        BigDecimal allocatedAmount,
        String allocatedCurrency,
        BigDecimal reservedAmount,
        String reservedCurrency,
        BigDecimal spentAmount,
        String spentCurrency,
        Long version) {}
