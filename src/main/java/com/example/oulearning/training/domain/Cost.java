package com.example.oulearning.training.domain;

import com.example.oulearning.budgeting.domain.BudgetingConstants;
import java.math.BigDecimal;
import java.math.RoundingMode;

public record Cost(BigDecimal amount, String currency) {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public Cost {
        amount = TrainingGuard.requireNonNegativeCost(amount).setScale(TrainingConstants.COST_SCALE, ROUNDING_MODE);
        currency = TrainingGuard.requireValidCurrency(currency);
    }

    public static Cost of(final BigDecimal amount, final String currency) {
        return new Cost(amount, currency);
    }

    public static Cost of(final BigDecimal amount) {
        return new Cost(amount, BudgetingConstants.DEFAULT_CURRENCY);
    }

    public static Cost of(final double amount, final String currency) {
        return new Cost(BigDecimal.valueOf(amount), currency);
    }

    public static Cost of(final double amount) {
        return new Cost(BigDecimal.valueOf(amount), BudgetingConstants.DEFAULT_CURRENCY);
    }

    public static Cost zero(final String currency) {
        return new Cost(BigDecimal.ZERO, currency);
    }

    public static Cost zero() {
        return new Cost(BigDecimal.ZERO, BudgetingConstants.DEFAULT_CURRENCY);
    }

    @Override
    public String toString() {
        return "%s %s".formatted(amount, currency);
    }
}
