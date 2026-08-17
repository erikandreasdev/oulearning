package com.example.oulearning.training.infrastructure.adapters;

import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.budgeting.domain.budget.repository.BudgetRepository;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.training.application.port.out.TrainingBudgetPort;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import javax.money.Monetary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Infrastructure adapter implementing {@link TrainingBudgetPort} by delegating to {@link BudgetRepository}.
 * Keeps the training application layer decoupled from budgeting domain internals.
 */
@Component
@Transactional
public class BudgetingTrainingBudgetAdapter implements TrainingBudgetPort {

    private final BudgetRepository budgetRepository;

    public BudgetingTrainingBudgetAdapter(BudgetRepository budgetRepository) {
        this.budgetRepository = Objects.requireNonNull(budgetRepository, "BudgetRepository cannot be null");
    }

    @Override
    public void reserveBudget(UUID ouId, int fiscalYear, BigDecimal amount, String currencyCode) {
        final var targetOuId = OuId.of(ouId);
        final var fy = FiscalYear.of(fiscalYear);
        final var budget = budgetRepository.findByOuIdAndFiscalYear(targetOuId, fy)
                .orElseThrow(() -> new NoSuchElementException(
                        "Budget not found for OU '%s' and Fiscal Year %d".formatted(ouId, fiscalYear)));

        final var currency = currencyCode != null ? Monetary.getCurrency(currencyCode) : budget.allocated().currency();
        final var money = Money.of(amount, currency);

        final var updated = budget.reserve(money, fy);
        budgetRepository.save(updated);
    }

    @Override
    public void consumeBudget(UUID ouId, int fiscalYear, BigDecimal amount, String currencyCode) {
        final var targetOuId = OuId.of(ouId);
        final var fy = FiscalYear.of(fiscalYear);
        final var budget = budgetRepository.findByOuIdAndFiscalYear(targetOuId, fy)
                .orElseThrow(() -> new NoSuchElementException(
                        "Budget not found for OU '%s' and Fiscal Year %d".formatted(ouId, fiscalYear)));

        final var currency = currencyCode != null ? Monetary.getCurrency(currencyCode) : budget.allocated().currency();
        final var money = Money.of(amount, currency);

        final var updated = budget.consumeReserved(money, fy);
        budgetRepository.save(updated);
    }

    @Override
    public void releaseBudget(UUID ouId, int fiscalYear, BigDecimal amount, String currencyCode) {
        final var targetOuId = OuId.of(ouId);
        final var fy = FiscalYear.of(fiscalYear);
        final var budget = budgetRepository.findByOuIdAndFiscalYear(targetOuId, fy)
                .orElseThrow(() -> new NoSuchElementException(
                        "Budget not found for OU '%s' and Fiscal Year %d".formatted(ouId, fiscalYear)));

        final var currency = currencyCode != null ? Monetary.getCurrency(currencyCode) : budget.allocated().currency();
        final var money = Money.of(amount, currency);

        final var updated = budget.releaseReservation(money, fy);
        budgetRepository.save(updated);
    }
}
