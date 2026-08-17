package com.example.oulearning.training.application.port.out;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Output port for training bounded context to interact with OU budgets
 * without importing domain classes from the budgeting bounded context.
 */
public interface TrainingBudgetPort {

    void reserveBudget(UUID ouId, int fiscalYear, BigDecimal amount, String currencyCode);

    void consumeBudget(UUID ouId, int fiscalYear, BigDecimal amount, String currencyCode);

    void releaseBudget(UUID ouId, int fiscalYear, BigDecimal amount, String currencyCode);
}
