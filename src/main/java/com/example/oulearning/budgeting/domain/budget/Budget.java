package com.example.oulearning.budgeting.domain.budget;

import com.example.oulearning.budgeting.domain.budget.exception.InsufficientBudgetException;
import com.example.oulearning.budgeting.domain.budget.exception.InvalidBudgetException;
import com.example.oulearning.organization.domain.unit.OuId;

/**
 * Aggregate Root representing the financial budget allocation and lifecycle of an organizational unit.
 * Tracks allocated funds, reserved commitments, and finalized expenses, calculating the available balance.
 *
 * @param id        the unique identifier of the budget
 * @param ouId      the organizational unit this budget belongs to
 * @param allocated the total funds allocated to this OU
 * @param reserved  the funds temporarily reserved for planned activities
 * @param spent     the finalized funds consumed
 */
public record Budget(BudgetId id, OuId ouId, Money allocated, Money reserved, Money spent) {

    public Budget {
        if (id == null) {
            throw new InvalidBudgetException("BudgetId cannot be null");
        }
        if (ouId == null) {
            throw new InvalidBudgetException("OuId cannot be null");
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

    public Budget reserve(Money amount) {
        if (amount == null || amount.isZero()) {
            return this;
        }
        if (!hasAvailableFunds(amount)) {
            throw new InsufficientBudgetException(
                    "Cannot reserve %s: available balance is only %s (allocated: %s, reserved: %s, spent: %s)"
                            .formatted(amount, available(), allocated, reserved, spent));
        }
        return new Budget(id, ouId, allocated, reserved.plus(amount), spent);
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
        return new Budget(id, ouId, allocated, reserved.minus(amount), spent);
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
        return new Budget(id, ouId, allocated, reserved.minus(amount), spent.plus(amount));
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
        return new Budget(id, ouId, allocated, reserved, spent.plus(amount));
    }

    public static Budget of(BudgetId id, OuId ouId, Money allocated) {
        if (allocated == null) {
            throw new InvalidBudgetException("Allocated money cannot be null");
        }
        final var zero = Money.zero(allocated.currency());
        return new Budget(id, ouId, allocated, zero, zero);
    }

    public static Budget zero(BudgetId id, OuId ouId) {
        final var zero = Money.zero();
        return new Budget(id, ouId, zero, zero, zero);
    }
}
