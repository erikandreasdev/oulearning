package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.budgeting.domain.budget.repository.BudgetRepository;
import com.example.oulearning.organization.domain.unit.OuId;
import java.util.Objects;
import java.util.UUID;
import javax.money.Monetary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating initial budget allocation for an OU.
 */
@Service
@Transactional
public class AllocateBudgetService implements AllocateBudgetUseCase {

    private final BudgetRepository repository;

    public AllocateBudgetService(BudgetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "BudgetRepository cannot be null");
    }

    @Override
    public UUID execute(AllocateBudgetCommand command) {
        Objects.requireNonNull(command, "AllocateBudgetCommand cannot be null");

        final var budgetId = command.budgetId() != null
                ? BudgetId.of(command.budgetId())
                : BudgetId.of(UUID.randomUUID());
        final var ouId = OuId.of(command.ouId());

        final var currency = command.currencyCode() != null
                ? Monetary.getCurrency(command.currencyCode())
                : Money.DEFAULT_CURRENCY;
        final var allocatedMoney = Money.of(command.amount(), currency);

        final var budget = Budget.of(budgetId, ouId, allocatedMoney);
        repository.save(budget);

        return budget.id().value();
    }
}
