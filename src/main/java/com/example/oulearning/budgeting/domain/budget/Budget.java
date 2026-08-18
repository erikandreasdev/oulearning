package com.example.oulearning.budgeting.domain.budget;

import com.example.oulearning.budgeting.domain.budget.exception.BudgetFiscalYearExpiredException;
import com.example.oulearning.budgeting.domain.budget.exception.InsufficientBudgetException;
import com.example.oulearning.budgeting.domain.budget.exception.InvalidBudgetException;
import com.example.oulearning.training.domain.request.vo.identity.OuId;
import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import java.util.Objects;

/**
 * Aggregate Root representing the financial budget allocation and lifecycle of an organizational unit for a specific fiscal year.
 * Tracks allocated funds, reserved commitments, and finalized expenses, calculating the available balance.
 * Enforces that budgets are valid and editable only for the current fiscal year and the immediately preceding one.
 *
 * @param id         the unique identifier of the budget
 * @param ouId       the organizational unit this budget belongs to
 * @param fiscalYear the fiscal year this budget applies to
 * @param allocated  the total funds allocated to this OU
 * @param reserved   the funds temporarily reserved for planned activities
 * @param spent      the finalized funds consumed
 */
public record Budget(
        BudgetId id,
        OuId ouId,
        FiscalYear fiscalYear,
        Money allocated,
        Money reserved,
        Money spent) {

    public Budget {
        if (id == null) {
            throw new InvalidBudgetException("BudgetId cannot be null");
        }
        if (ouId == null) {
            throw new InvalidBudgetException("OuId cannot be null");
        }
        if (fiscalYear == null) {
            throw new InvalidBudgetException("FiscalYear cannot be null");
        }
        if (allocated == null) {
            throw new InvalidBudgetException("Allocated money cannot be null");
        }
        if (reserved == null) {
            throw new InvalidBudgetException("Reserved money cannot be null");
        }
        if (spent == null) {
            throw new InvalidBudgetException("Spent money cannot be null");
        }

        final var totalCommitted = reserved.plus(spent);
        if (totalCommitted.compareTo(allocated) > 0) {
            throw new InvalidBudgetException(
                    "Total committed funds (reserved: %s + spent: %s = %s) cannot exceed allocated budget: %s"
                            .formatted(reserved, spent, totalCommitted, allocated));
        }
    }

    public Money available() {
        return allocated.minus(reserved.plus(spent));
    }

    public boolean hasAvailableFunds(Money amount) {
        if (amount == null) {
            return false;
        }
        return available().compareTo(amount) >= 0;
    }

    public boolean isEditable(FiscalYear currentFiscalYear) {
        if (currentFiscalYear == null) {
            return false;
        }
        return fiscalYear.isCurrentOrPrevious(currentFiscalYear);
    }

    public void validateEditable(FiscalYear currentFiscalYear) {
        Objects.requireNonNull(currentFiscalYear, "Current FiscalYear cannot be null");
        if (!isEditable(currentFiscalYear)) {
            throw new BudgetFiscalYearExpiredException(
                    "Budget for fiscal year %s is expired and cannot be modified in current fiscal year %s"
                            .formatted(fiscalYear, currentFiscalYear));
        }
    }

    public Budget reserve(Money amount) {
        if (amount == null || amount.isZero()) {
            return this;
        }
        if (!hasAvailableFunds(amount)) {
            throw new InsufficientBudgetException(
                    "Cannot reserve %s: available balance is only %s (allocated: %s, reserved: %s, spent: %s)"
                            .formatted(amount, available(), allocated, reserved, spent));
        }
        return new Budget(id, ouId, fiscalYear, allocated, reserved.plus(amount), spent);
    }

    public Budget reserve(Money amount, FiscalYear currentFiscalYear) {
        validateEditable(currentFiscalYear);
        return reserve(amount);
    }

    public Budget releaseReservation(Money amount) {
        if (amount == null || amount.isZero()) {
            return this;
        }
        if (amount.compareTo(reserved) > 0) {
            throw new InvalidBudgetException(
                    "Cannot release reservation of %s: currently reserved amount is only %s"
                            .formatted(amount, reserved));
        }
        return new Budget(id, ouId, fiscalYear, allocated, reserved.minus(amount), spent);
    }

    public Budget releaseReservation(Money amount, FiscalYear currentFiscalYear) {
        validateEditable(currentFiscalYear);
        return releaseReservation(amount);
    }

    public Budget consumeReserved(Money amount) {
        if (amount == null || amount.isZero()) {
            return this;
        }
        if (amount.compareTo(reserved) > 0) {
            throw new InvalidBudgetException(
                    "Cannot consume %s from reservation: currently reserved amount is only %s"
                            .formatted(amount, reserved));
        }
        return new Budget(id, ouId, fiscalYear, allocated, reserved.minus(amount), spent.plus(amount));
    }

    public Budget consumeReserved(Money amount, FiscalYear currentFiscalYear) {
        validateEditable(currentFiscalYear);
        return consumeReserved(amount);
    }

    public Budget spendDirect(Money amount) {
        if (amount == null || amount.isZero()) {
            return this;
        }
        if (!hasAvailableFunds(amount)) {
            throw new InsufficientBudgetException(
                    "Cannot spend %s directly: available balance is only %s (allocated: %s, reserved: %s, spent: %s)"
                            .formatted(amount, available(), allocated, reserved, spent));
        }
        return new Budget(id, ouId, fiscalYear, allocated, reserved, spent.plus(amount));
    }

    public Budget spendDirect(Money amount, FiscalYear currentFiscalYear) {
        validateEditable(currentFiscalYear);
        return spendDirect(amount);
    }

    public static Budget of(BudgetId id, OuId ouId, FiscalYear fiscalYear, Money allocated) {
        if (allocated == null) {
            throw new InvalidBudgetException("Allocated money cannot be null");
        }
        final var zero = Money.zero(allocated.currency());
        return new Budget(id, ouId, fiscalYear, allocated, zero, zero);
    }

    public static Budget of(BudgetId id, OuId ouId, Money allocated) {
        return of(id, ouId, FiscalYear.of(2026), allocated);
    }

    public static Budget zero(BudgetId id, OuId ouId, FiscalYear fiscalYear) {
        final var zero = Money.zero();
        return new Budget(id, ouId, fiscalYear, zero, zero, zero);
    }

    public static Budget zero(BudgetId id, OuId ouId) {
        return zero(id, ouId, FiscalYear.of(2026));
    }
}
