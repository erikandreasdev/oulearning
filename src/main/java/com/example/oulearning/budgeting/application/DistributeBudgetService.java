package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.budgeting.domain.budget.repository.BudgetRepository;
import com.example.oulearning.budgeting.domain.distribution.BudgetDistributionService;
import com.example.oulearning.budgeting.domain.distribution.BudgetDistributionStrategy;
import com.example.oulearning.organization.domain.unit.OuId;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.money.Monetary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating parent budget distribution among child OUs.
 */
@Service
@Transactional
public class DistributeBudgetService implements DistributeBudgetUseCase {

    private final BudgetRepository budgetRepository;
    private final BudgetDistributionService distributionService = new BudgetDistributionService();

    public DistributeBudgetService(BudgetRepository budgetRepository) {
        this.budgetRepository = Objects.requireNonNull(budgetRepository, "BudgetRepository cannot be null");
    }

    @Override
    public BudgetDistributionResult execute(DistributeBudgetCommand command) {
        Objects.requireNonNull(command, "DistributeBudgetCommand cannot be null");

        final var parentOuId = OuId.of(command.parentOuId());
        final var parentBudget = budgetRepository.findByOuId(parentOuId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Parent budget for OU '%s' not found".formatted(command.parentOuId())));

        final var strategy = command.strategyType() != null ? command.strategyType().toUpperCase() : "EXCLUSIVE";
        final BudgetDistributionStrategy distributionStrategy;

        switch (strategy) {
            case "EXCLUSIVE" -> distributionStrategy = new BudgetDistributionStrategy.ExclusiveAllocation();
            case "EQUAL" -> distributionStrategy = new BudgetDistributionStrategy.EqualDistribution();
            case "EXPLICIT" -> {
                final var currency = command.currencyCode() != null
                        ? Monetary.getCurrency(command.currencyCode())
                        : parentBudget.allocated().currency();
                final var explicitAllocations = new HashMap<OuId, Money>();
                for (final var entry : command.explicitAllocations().entrySet()) {
                    explicitAllocations.put(OuId.of(entry.getKey()), Money.of(entry.getValue(), currency));
                }
                distributionStrategy = new BudgetDistributionStrategy.ExplicitDistribution(explicitAllocations);
            }
            default -> throw new IllegalArgumentException("Unknown distribution strategy: " + strategy);
        }

        final var childOuIds = command.childOuIds().stream().map(OuId::of).collect(Collectors.toSet());
        final var childBudgets = distributionService.distribute(
                parentBudget,
                childOuIds,
                distributionStrategy,
                () -> BudgetId.of(UUID.randomUUID()));

        for (final var childBudget : childBudgets) {
            budgetRepository.save(childBudget);
        }

        return new BudgetDistributionResult(parentBudget, childBudgets);
    }
}
