package com.example.oulearning.budgeting.domain;

import java.util.UUID;

/**
 * Value object representing a budget identifier.
 *
 * @param value the UUID value
 */
public record Id(UUID value) {

    public Id {
        if (value == null) {
            throw new InvalidBudgetOperationException("Budget id cannot be null");
        }
    }

    public static Id of(UUID value) {
        return new Id(value);
    }

    public static Id fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidBudgetOperationException("Budget id string cannot be null or blank");
        }
        try {
            return new Id(UUID.fromString(value.strip()));
        } catch (IllegalArgumentException e) {
            throw new InvalidBudgetOperationException("Invalid UUID format: " + value);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
