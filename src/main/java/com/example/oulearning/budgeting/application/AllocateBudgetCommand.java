package com.example.oulearning.budgeting.application;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Immutable application command for allocating budget to an OU.
 */
public record AllocateBudgetCommand(
        UUID budgetId,
        UUID ouId,
        BigDecimal amount,
        String currencyCode) {}
