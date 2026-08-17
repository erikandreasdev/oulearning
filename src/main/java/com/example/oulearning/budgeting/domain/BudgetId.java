package com.example.oulearning.budgeting.domain;

import java.util.UUID;

/**
 * Strongly-typed identity value object for a Budget aggregate.
 *
 * @param value the underlying {@link UUID}
 */
public record BudgetId(UUID value) {

    /**
     * Compact constructor enforcing non-null ID.
     */
    public BudgetId {
        if (value == null) {
            throw new InvalidBudgetException("BudgetId cannot be null");
        }
    }

    /**
     * Factory method creating a {@link BudgetId} from a {@link UUID}.
     *
     * @param value the UUID
     * @return the {@link BudgetId}
     */
    public static BudgetId of(UUID value) {
        return new BudgetId(value);
    }

    /**
     * Factory method creating a {@link BudgetId} from a string representation.
     *
     * @param rawValue the UUID string
     * @return the {@link BudgetId}
     */
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
