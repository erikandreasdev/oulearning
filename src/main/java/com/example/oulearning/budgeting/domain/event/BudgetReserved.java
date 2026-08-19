package com.example.oulearning.budgeting.domain.event;

import com.example.oulearning.budgeting.domain.Id;
import com.example.oulearning.budgeting.domain.Money;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when an amount is reserved in a budget.
 */
public record BudgetReserved(
        Id budgetId,
        Money amount,
        Money newReserved,
        Money newAvailable,
        Instant occurredAt) {

    public BudgetReserved {
        Objects.requireNonNull(budgetId, "budgetId cannot be null");
        Objects.requireNonNull(amount, "amount cannot be null");
        Objects.requireNonNull(newReserved, "newReserved cannot be null");
        Objects.requireNonNull(newAvailable, "newAvailable cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}
