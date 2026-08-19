package com.example.oulearning.budgeting.domain.event;

import com.example.oulearning.budgeting.domain.Id;
import com.example.oulearning.budgeting.domain.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when additional funds are allocated to a budget.
 */
public record BudgetAllocated(
        Id budgetId,
        Money additionalAmount,
        Money newTotal,
        Money newAvailable,
        Instant occurredAt) {

    public BudgetAllocated {
        Objects.requireNonNull(budgetId, "budgetId cannot be null");
        Objects.requireNonNull(additionalAmount, "additionalAmount cannot be null");
        Objects.requireNonNull(newTotal, "newTotal cannot be null");
        Objects.requireNonNull(newAvailable, "newAvailable cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}
