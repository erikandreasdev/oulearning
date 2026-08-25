package com.example.oulearning.budgeting.domain.model;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.util.Objects;

public record Budget(
        BudgetId id,
        OrganizationalUnitId organizationalUnitId,
        FiscalYear fiscalYear,
        Money total,
        Money reserved,
        Money available,
        boolean active) {

    public Budget {
        BudgetingGuard.requireBudgetId(id);
        BudgetingGuard.requireOrganizationalUnitId(organizationalUnitId);
        BudgetingGuard.requireFiscalYear(fiscalYear);
        BudgetingGuard.requireTotal(total);
        BudgetingGuard.requireReserved(reserved);
        BudgetingGuard.requireAvailable(available);
    }

    public static Budget create(
            final BudgetId id,
            final OrganizationalUnitId organizationalUnitId,
            final FiscalYear fiscalYear,
            final Money total,
            final Money reserved,
            final Money available) {
        return new Budget(id, organizationalUnitId, fiscalYear, total, reserved, available, true);
    }

    public static Budget reconstitute(
            final BudgetId id,
            final OrganizationalUnitId organizationalUnitId,
            final FiscalYear fiscalYear,
            final Money total,
            final Money reserved,
            final Money available,
            final boolean active) {
        return new Budget(id, organizationalUnitId, fiscalYear, total, reserved, available, active);
    }

    public static Budget of(
            final BudgetId id,
            final OrganizationalUnitId organizationalUnitId,
            final FiscalYear fiscalYear,
            final Money total,
            final Money reserved,
            final Money available) {
        return create(id, organizationalUnitId, fiscalYear, total, reserved, available);
    }

    public Budget updateAmounts(final Money total, final Money reserved, final Money available) {
        BudgetingGuard.requireTotal(total);
        BudgetingGuard.requireReserved(reserved);
        BudgetingGuard.requireAvailable(available);
        return new Budget(id, organizationalUnitId, fiscalYear, total, reserved, available, active);
    }

    public Budget deactivate() {
        return new Budget(id, organizationalUnitId, fiscalYear, total, reserved, available, false);
    }

    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof final Budget budget && Objects.equals(id, budget.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
