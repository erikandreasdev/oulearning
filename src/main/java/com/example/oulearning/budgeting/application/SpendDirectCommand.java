package com.example.oulearning.budgeting.application;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Immutable application command for direct spend from available budget.
 */
public record SpendDirectCommand(
        UUID budgetId,
        BigDecimal amount,
        String currencyCode) {}
