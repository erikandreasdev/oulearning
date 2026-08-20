package com.example.oulearning.budgeting.domain;

import com.example.oulearning.budgeting.domain.exception.InvalidBudgetOperationException;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import java.math.BigDecimal;
import javax.money.MonetaryAmount;

final class BudgetingGuard {

    private static final String FIELD_BUDGET_ID = "Budget id";
    private static final String FIELD_OU_ID = "Organizational unit id";
    private static final String FIELD_FISCAL_YEAR = "Fiscal year";
    private static final String FIELD_TOTAL = "Total";
    private static final String FIELD_RESERVED = "Reserved";
    private static final String FIELD_AVAILABLE = "Available";
    private static final String FIELD_MONETARY_AMOUNT = "Monetary amount";
    private static final String FIELD_MONEY_AMOUNT = "Money amount";
    private static final String FIELD_MONEY_TO_ADD = "Money to add";
    private static final String FIELD_MONEY_TO_SUBTRACT = "Money to subtract";
    private static final String FIELD_MONEY_TO_COMPARE = "Money to compare";

    private BudgetingGuard() {
    }

    static void requireBudgetId(final BudgetId id) {
        requireNonNull(id, FIELD_BUDGET_ID);
    }

    static void requirePositiveBudgetId(final long value) {
        requirePositiveId(value, FIELD_BUDGET_ID);
    }

    static long requireValidBudgetId(final String value) {
        return requireValidId(value, FIELD_BUDGET_ID);
    }

    static void requireOrganizationalUnitId(final OrganizationalUnitId organizationalUnitId) {
        requireNonNull(organizationalUnitId, FIELD_OU_ID);
    }

    static void requireFiscalYear(final FiscalYear fiscalYear) {
        requireNonNull(fiscalYear, FIELD_FISCAL_YEAR);
    }

    static void requireFiscalYearBetween(final int value, final int min, final int max) {
        if (value < min || value > max) {
            throw InvalidBudgetOperationException.fiscalYearOutOfRange(min, max, value);
        }
    }

    static void requireTotal(final Money total) {
        requireNonNull(total, FIELD_TOTAL);
    }

    static void requireReserved(final Money reserved) {
        requireNonNull(reserved, FIELD_RESERVED);
    }

    static void requireAvailable(final Money available) {
        requireNonNull(available, FIELD_AVAILABLE);
    }

    static void requireMonetaryAmount(final MonetaryAmount amount) {
        requireNonNull(amount, FIELD_MONETARY_AMOUNT);
    }

    static void requireMoneyAmount(final BigDecimal amount) {
        requireNonNull(amount, FIELD_MONEY_AMOUNT);
    }

    static void requireMoneyToAdd(final Money other) {
        requireNonNull(other, FIELD_MONEY_TO_ADD);
    }

    static void requireMoneyToSubtract(final Money other) {
        requireNonNull(other, FIELD_MONEY_TO_SUBTRACT);
    }

    static void requireMoneyToCompare(final Money other) {
        requireNonNull(other, FIELD_MONEY_TO_COMPARE);
    }

    private static <T> void requireNonNull(final T value, final String fieldName) {
        if (value == null) {
            throw InvalidBudgetOperationException.nullField(fieldName);
        }
    }

    private static void requirePositiveId(final long value, final String fieldName) {
        if (value < BudgetingConstants.MIN_ID) {
            throw InvalidBudgetOperationException.nonPositiveId(fieldName, value);
        }
    }

    private static long requireValidId(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw InvalidBudgetOperationException.nullOrBlank(fieldName);
        }
        try {
            final var parsed = Long.parseLong(value.strip());
            requirePositiveId(parsed, fieldName);
            return parsed;
        } catch (final NumberFormatException e) {
            throw InvalidBudgetOperationException.invalidId(fieldName, value, e);
        }
    }
}
