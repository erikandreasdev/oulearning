package com.example.oulearning.budgeting.application.port.in.command;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Immutable application command for reserving funds from a budget.
 */
public record ReserveFundsCommand(
        UUID budgetId,
        BigDecimal amount,
        String currencyCode) {}
