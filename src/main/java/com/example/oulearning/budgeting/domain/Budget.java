package com.example.oulearning.budgeting.domain;

import com.example.oulearning.organization.domain.hierarchy.OuId;
import java.util.Objects;

/**
 * Domain object representing a budget.
 */
public final class Budget {

    private final BudgetId id;
    private final OuId ouId;
    private final FiscalYear fiscalYear;
    private final Money total;
    private final Money reserved;
    private final Money available;

    public Budget(
            final BudgetId id,
            final OuId ouId,
            final FiscalYear fiscalYear,
            final Money total,
            final Money reserved,
            final Money available) {
        this.id = BudgetingGuard.requireNonNull(id, "Budget id");
        this.ouId = BudgetingGuard.requireNonNull(ouId, "Ou id");
        this.fiscalYear = BudgetingGuard.requireNonNull(fiscalYear, "FiscalYear");
        this.total = BudgetingGuard.requireNonNull(total, "Total");
        this.reserved = BudgetingGuard.requireNonNull(reserved, "Reserved");
        this.available = BudgetingGuard.requireNonNull(available, "Available");
    }

    public static Budget of(
            final BudgetId id,
            final OuId ouId,
            final FiscalYear fiscalYear,
            final Money total,
            final Money reserved,
            final Money available) {
        return new Budget(id, ouId, fiscalYear, total, reserved, available);
    }

    public BudgetId id() {
        return id;
    }

    public OuId ouId() {
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

    @Override
    public boolean equals(final Object o) {
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
        return "Budget[id=%s, ouId=%s, fiscalYear=%s, total=%s, reserved=%s, available=%s]"
                .formatted(id, ouId, fiscalYear, total, reserved, available);
    }
}
