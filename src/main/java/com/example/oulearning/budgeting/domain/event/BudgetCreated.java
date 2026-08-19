package com.example.oulearning.budgeting.domain.event;

import com.example.oulearning.budgeting.domain.FiscalYear;
import com.example.oulearning.budgeting.domain.Id;
import com.example.oulearning.budgeting.domain.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a budget is created for an organizational unit.
 */
public record BudgetCreated(
        Id budgetId,
        com.example.oulearning.organization.domain.hierarchy.Id ouId,
        FiscalYear fiscalYear,
        Money total,
        Instant occurredAt) {

    public BudgetCreated {
        Objects.requireNonNull(budgetId, "budgetId cannot be null");
        Objects.requireNonNull(ouId, "ouId cannot be null");
        Objects.requireNonNull(fiscalYear, "fiscalYear cannot be null");
        Objects.requireNonNull(total, "total cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}
