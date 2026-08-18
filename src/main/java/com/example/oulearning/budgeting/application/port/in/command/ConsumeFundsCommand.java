package com.example.oulearning.budgeting.application.port.in.command;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Immutable application command for consuming reserved funds (spending reserved money).
 */
public record ConsumeFundsCommand(
        UUID budgetId,
        BigDecimal amount,
        String currencyCode) {}
