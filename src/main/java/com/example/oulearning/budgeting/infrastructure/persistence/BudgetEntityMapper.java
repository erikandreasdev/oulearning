package com.example.oulearning.budgeting.infrastructure.persistence;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import java.util.Objects;
import javax.money.Monetary;
import org.springframework.stereotype.Component;

/**
 * Specific mapper responsible for bidirectional mapping between {@link Budget} domain aggregates
 * and {@link BudgetEntity} persistence entities.
 */
@Component
public class BudgetEntityMapper {

    /**
     * Maps a {@link Budget} domain aggregate to a {@link BudgetEntity} persistence entity.
     *
     * @param domain  the budget aggregate
     * @param version the optimistic locking version
     * @return the persistence entity
     */
    public BudgetEntity toEntity(Budget domain, Long version) {
        Objects.requireNonNull(domain, "Budget domain model cannot be null");
        return new BudgetEntity(
                domain.id().toString(),
                domain.ouId().toString(),
                domain.fiscalYear().value(),
                domain.allocated().amount(),
                domain.allocated().currency().getCurrencyCode(),
                domain.reserved().amount(),
                domain.reserved().currency().getCurrencyCode(),
                domain.spent().amount(),
                domain.spent().currency().getCurrencyCode(),
                version != null ? version : 0L);
    }

    /**
     * Maps a {@link BudgetEntity} persistence entity to a {@link Budget} domain aggregate.
     *
     * @param entity the persistence entity
     * @return the reconstructed {@link Budget} domain aggregate
     */
    public Budget toDomain(BudgetEntity entity) {
        Objects.requireNonNull(entity, "BudgetEntity cannot be null");

        final var budgetId = BudgetId.of(entity.id());
        final var ouId = OuId.of(entity.ouId());
        final var fiscalYear = FiscalYear.of(entity.fiscalYear() != null ? entity.fiscalYear() : 2026);

        final var allocatedCurrency = Monetary.getCurrency(entity.allocatedCurrency());
        final var reservedCurrency = Monetary.getCurrency(entity.reservedCurrency());
        final var spentCurrency = Monetary.getCurrency(entity.spentCurrency());

        final var allocated = Money.of(entity.allocatedAmount(), allocatedCurrency);
        final var reserved = Money.of(entity.reservedAmount(), reservedCurrency);
        final var spent = Money.of(entity.spentAmount(), spentCurrency);

        return new Budget(budgetId, ouId, fiscalYear, allocated, reserved, spent);
    }
}
