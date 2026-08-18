package com.example.oulearning.budgeting.application.port.in.command;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Immutable application command for releasing reserved funds back to available budget.
 */
public record ReleaseFundsCommand(
        UUID budgetId,
        BigDecimal amount,
        String currencyCode) {}
