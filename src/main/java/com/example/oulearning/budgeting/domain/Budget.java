package com.example.oulearning.budgeting.domain;

import com.example.oulearning.budgeting.domain.event.BudgetAllocated;
import com.example.oulearning.budgeting.domain.event.BudgetCreated;
import com.example.oulearning.budgeting.domain.event.BudgetReserved;
import com.example.oulearning.budgeting.domain.event.ReservationReleased;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root representing a budget for an organizational unit and fiscal year.
 */
public final class Budget {

    private final Id id;
    private final com.example.oulearning.organization.domain.hierarchy.Id ouId;
    private final FiscalYear fiscalYear;
    private Money total;
    private Money reserved;
    private Money available;
    private final List<Object> domainEvents = new ArrayList<>();

    private Budget(
            Id id,
            com.example.oulearning.organization.domain.hierarchy.Id ouId,
            FiscalYear fiscalYear,
            Money total,
            Money reserved,
            Money available) {
        this.id = Objects.requireNonNull(id, "Budget id cannot be null");
        this.ouId = Objects.requireNonNull(ouId, "Ou id cannot be null");
        this.fiscalYear = Objects.requireNonNull(fiscalYear, "FiscalYear cannot be null");
        this.total = Objects.requireNonNull(total, "Total cannot be null");
        this.reserved = Objects.requireNonNull(reserved, "Reserved cannot be null");
        this.available = Objects.requireNonNull(available, "Available cannot be null");

        if (total.isNegative()) {
            throw new InvalidBudgetOperationException("Total budget cannot be negative: " + total);
        }
        if (reserved.isNegative()) {
            throw new InvalidBudgetOperationException("Reserved budget cannot be negative: " + reserved);
        }
        if (available.isNegative()) {
            throw new InvalidBudgetOperationException("Available budget cannot be negative: " + available);
        }
        if (!total.equals(reserved.add(available))) {
            throw new InvalidBudgetOperationException(
                    "Budget invariant violated: total (" + total + ") must equal reserved (" + reserved + ") + available (" + available + ")");
        }
    }

    /**
     * Factory method to create a new {@link Budget} and register {@link BudgetCreated}.
     */
    public static Budget create(
            Id id,
            com.example.oulearning.organization.domain.hierarchy.Id ouId,
            FiscalYear fiscalYear,
            Money total,
            Instant createdAt) {
        Objects.requireNonNull(total, "Total cannot be null");
        Money zero = Money.zero(total.currency());
        Budget budget = new Budget(id, ouId, fiscalYear, total, zero, total);
        budget.registerEvent(new BudgetCreated(id, ouId, fiscalYear, total, Objects.requireNonNull(createdAt, "createdAt cannot be null")));
        return budget;
    }

    /**
     * Reconstitutes an existing {@link Budget} from persistence.
     */
    public static Budget reconstitute(
            Id id,
            com.example.oulearning.organization.domain.hierarchy.Id ouId,
            FiscalYear fiscalYear,
            Money total,
            Money reserved,
            Money available) {
        return new Budget(id, ouId, fiscalYear, total, reserved, available);
    }

    public void reserve(Money amount, Instant occurredAt) {
        Objects.requireNonNull(amount, "Amount to reserve cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (amount.isNegative() || amount.isZero()) {
            throw new InvalidBudgetOperationException("Reservation amount must be strictly positive: " + amount);
        }
        if (amount.isGreaterThan(this.available)) {
            throw new InsufficientBudgetException(
                    "Cannot reserve " + amount + "; available budget is only " + this.available);
        }

        this.reserved = this.reserved.add(amount);
        this.available = this.available.subtract(amount);
        registerEvent(new BudgetReserved(this.id, amount, this.reserved, this.available, occurredAt));
    }

    public void releaseReservation(Money amount, Instant occurredAt) {
        Objects.requireNonNull(amount, "Amount to release cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (amount.isNegative() || amount.isZero()) {
            throw new InvalidBudgetOperationException("Release amount must be strictly positive: " + amount);
        }
        if (amount.isGreaterThan(this.reserved)) {
            throw new InvalidBudgetOperationException(
                    "Cannot release " + amount + "; currently reserved budget is only " + this.reserved);
        }

        this.reserved = this.reserved.subtract(amount);
        this.available = this.available.add(amount);
        registerEvent(new ReservationReleased(this.id, amount, this.reserved, this.available, occurredAt));
    }

    public void allocate(Money additionalAmount, Instant occurredAt) {
        Objects.requireNonNull(additionalAmount, "Additional amount cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (additionalAmount.isNegative() || additionalAmount.isZero()) {
            throw new InvalidBudgetOperationException("Allocation amount must be strictly positive: " + additionalAmount);
        }

        this.total = this.total.add(additionalAmount);
        this.available = this.available.add(additionalAmount);
        registerEvent(new BudgetAllocated(this.id, additionalAmount, this.total, this.available, occurredAt));
    }

    public Id id() {
        return id;
    }

    public com.example.oulearning.organization.domain.hierarchy.Id ouId() {
        return ouId;
    }

    public FiscalYear fiscalYear() {
        return fiscalYear;
    }

    public Money total() {
        return total;
    }

    public Money reserved() {
        return reserved;
    }

    public Money available() {
        return available;
    }

    private void registerEvent(Object event) {
        this.domainEvents.add(event);
    }

    public List<Object> pullDomainEvents() {
        List<Object> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return Collections.unmodifiableList(events);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Budget budget)) return false;
        return Objects.equals(id, budget.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Budget[id=" + id + ", ouId=" + ouId + ", fiscalYear=" + fiscalYear + ", total=" + total + "]";
    }
}
