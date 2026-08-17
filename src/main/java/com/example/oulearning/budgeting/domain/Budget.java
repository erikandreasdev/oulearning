package com.example.oulearning.budgeting.domain;

import com.example.oulearning.shared.domain.Money;
import com.example.oulearning.shared.domain.OuId;

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

    /**
     * Compact constructor enforcing non-null invariants, currency alignment, and fund constraints.
     */
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

    /**
     * Calculates the currently available uncommitted budget funds.
     *
     * @return the available {@link Money} balance
     */
    public Money available() {
        return allocated.minus(reserved.plus(spent));
    }

    /**
     * Checks if the budget has sufficient available funds for a requested amount.
     *
     * @param amount the amount to check
     * @return {@code true} if available funds >= amount
     */
    public boolean hasAvailableFunds(Money amount) {
        if (amount == null) {
            return false;
        }
        return available().compareTo(amount) >= 0;
    }

    /**
     * Reserves funds for a planned activity, moving them from available to reserved.
     *
     * @param amount the amount to reserve
     * @return an updated {@link Budget} instance
     */
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

    /**
     * Releases a previously held reservation, returning the funds to the available balance.
     *
     * @param amount the amount to release
     * @return an updated {@link Budget} instance
     */
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

    /**
     * Consumes previously reserved funds upon finalizing an expense, moving funds from reserved to spent.
     *
     * @param amount the amount consumed
     * @return an updated {@link Budget} instance
     */
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

    /**
     * Directly spends funds from the available balance without prior reservation.
     *
     * @param amount the amount to spend
     * @return an updated {@link Budget} instance
     */
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

    /**
     * Factory method creating a initial {@link Budget} with allocated funds (reserved = 0, spent = 0).
     *
     * @param id        the budget ID
     * @param ouId      the organizational unit ID
     * @param allocated the allocated funds
     * @return a new {@link Budget}
     */
    public static Budget of(BudgetId id, OuId ouId, Money allocated) {
        if (allocated == null) {
            throw new InvalidBudgetException("Allocated money cannot be null");
        }
        final var zero = Money.zero(allocated.currency());
        return new Budget(id, ouId, allocated, zero, zero);
    }

    /**
     * Factory method creating a default zero budget for an OU (allocated = 0, reserved = 0, spent = 0).
     *
     * @param id   the budget ID
     * @param ouId the organizational unit ID
     * @return a new {@link Budget} with zero funds
     */
    public static Budget zero(BudgetId id, OuId ouId) {
        final var zero = Money.zero();
        return new Budget(id, ouId, zero, zero, zero);
    }
}
