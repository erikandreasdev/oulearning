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
            BudgetId id,
            OuId ouId,
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
    }

    public static Budget of(
            BudgetId id,
            OuId ouId,
            FiscalYear fiscalYear,
            Money total,
            Money reserved,
            Money available) {
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
        return "Budget[id=" + id + ", ouId=" + ouId + ", fiscalYear=" + fiscalYear + ", total=" + total
                + ", reserved=" + reserved + ", available=" + available + "]";
    }
}
