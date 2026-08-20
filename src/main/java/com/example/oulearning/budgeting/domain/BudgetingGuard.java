package com.example.oulearning.budgeting.domain;

import com.example.oulearning.budgeting.domain.exception.InvalidBudgetOperationException;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import java.math.BigDecimal;
import javax.money.MonetaryAmount;

final class BudgetingGuard {

    private BudgetingGuard() {
    }

    static BudgetId requireBudgetId(final BudgetId id) {
        return requireNonNull(id, "Budget id");
    }

    static long requireBudgetId(final long value) {
        return requirePositiveId(value, "Budget id");
    }

    static long requireValidBudgetId(final String value) {
        return requireValidId(value, "Budget id");
    }

    static OrganizationalUnitId requireOrganizationalUnitId(final OrganizationalUnitId organizationalUnitId) {
        return requireNonNull(organizationalUnitId, "Organizational unit id");
    }

    static FiscalYear requireFiscalYear(final FiscalYear fiscalYear) {
        return requireNonNull(fiscalYear, "Fiscal year");
    }

    static int requireFiscalYearBetween(final int value, final int min, final int max) {
        if (value < min || value > max) {
            throw InvalidBudgetOperationException.fiscalYearOutOfRange(min, max, value);
        }
        return value;
    }

    static Money requireTotal(final Money total) {
        return requireNonNull(total, "Total");
    }

    static Money requireReserved(final Money reserved) {
        return requireNonNull(reserved, "Reserved");
    }

    static Money requireAvailable(final Money available) {
        return requireNonNull(available, "Available");
    }

    static MonetaryAmount requireMonetaryAmount(final MonetaryAmount amount) {
        return requireNonNull(amount, "Monetary amount");
    }

    static BigDecimal requireMoneyAmount(final BigDecimal amount) {
        return requireNonNull(amount, "Money amount");
    }

    static Money requireMoneyToAdd(final Money other) {
        return requireNonNull(other, "Money to add");
    }

    static Money requireMoneyToSubtract(final Money other) {
        return requireNonNull(other, "Money to subtract");
    }

    static Money requireMoneyToCompare(final Money other) {
        return requireNonNull(other, "Money to compare");
    }

    private static <T> T requireNonNull(final T value, final String fieldName) {
        if (value == null) {
            throw InvalidBudgetOperationException.nullField(fieldName);
        }
        return value;
    }

    private static long requirePositiveId(final long value, final String fieldName) {
        if (value < BudgetingConstants.MIN_ID) {
            throw InvalidBudgetOperationException.nonPositiveId(fieldName, value);
        }
        return value;
    }

    private static long requireValidId(final String value, final String fieldName) {
        if (value == null || value.isBlank()) {
            throw InvalidBudgetOperationException.nullOrBlank(fieldName);
        }
        try {
            final var parsed = Long.parseLong(value.strip());
            return requirePositiveId(parsed, fieldName);
        } catch (final NumberFormatException e) {
            throw InvalidBudgetOperationException.invalidId(fieldName, value, e);
        }
    }
}
