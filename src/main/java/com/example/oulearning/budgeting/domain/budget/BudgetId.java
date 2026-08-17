package com.example.oulearning.budgeting.domain.budget;

import com.example.oulearning.budgeting.domain.budget.exception.InvalidBudgetException;
import java.util.UUID;

/**
 * Strongly-typed identity value object for a Budget aggregate.
 */
public record BudgetId(UUID value) {

    public BudgetId {
        if (value == null) {
            throw new InvalidBudgetException("BudgetId cannot be null");
        }
    }

    public static BudgetId of(UUID value) {
        return new BudgetId(value);
    }

    public static BudgetId of(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidBudgetException("BudgetId string representation cannot be null or blank");
        }
        try {
            return new BudgetId(UUID.fromString(rawValue.strip()));
        } catch (IllegalArgumentException e) {
            throw new InvalidBudgetException("Invalid UUID format for BudgetId: '%s'".formatted(rawValue));
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
