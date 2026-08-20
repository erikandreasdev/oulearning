package com.example.oulearning.budgeting.domain;

import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import java.util.Objects;

public record Budget(
        BudgetId id,
        OrganizationalUnitId organizationalUnitId,
        FiscalYear fiscalYear,
        Money total,
        Money reserved,
        Money available) {

    public Budget {
        BudgetingGuard.requireBudgetId(id);
        BudgetingGuard.requireOrganizationalUnitId(organizationalUnitId);
        BudgetingGuard.requireFiscalYear(fiscalYear);
        BudgetingGuard.requireTotal(total);
        BudgetingGuard.requireReserved(reserved);
        BudgetingGuard.requireAvailable(available);
    }

    public static Budget of(
            final BudgetId id,
            final OrganizationalUnitId organizationalUnitId,
            final FiscalYear fiscalYear,
            final Money total,
            final Money reserved,
            final Money available) {
        return new Budget(id, organizationalUnitId, fiscalYear, total, reserved, available);
    }

    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof final Budget budget && Objects.equals(id, budget.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Budget[id=%s, organizationalUnitId=%s, fiscalYear=%s, total=%s, reserved=%s, available=%s]"
                .formatted(id, organizationalUnitId, fiscalYear, total, reserved, available);
    }
}
