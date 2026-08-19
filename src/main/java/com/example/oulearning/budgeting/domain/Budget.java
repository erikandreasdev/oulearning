package com.example.oulearning.budgeting.domain;

import com.example.oulearning.organization.domain.hierarchy.OuId;
import java.util.Objects;

public record Budget(
        BudgetId id,
        OuId ouId,
        FiscalYear fiscalYear,
        Money total,
        Money reserved,
        Money available) {

    public Budget {
        id = BudgetingGuard.requireNonNull(id, "Budget id");
        ouId = BudgetingGuard.requireNonNull(ouId, "Ou id");
        fiscalYear = BudgetingGuard.requireNonNull(fiscalYear, "FiscalYear");
        total = BudgetingGuard.requireNonNull(total, "Total");
        reserved = BudgetingGuard.requireNonNull(reserved, "Reserved");
        available = BudgetingGuard.requireNonNull(available, "Available");
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
