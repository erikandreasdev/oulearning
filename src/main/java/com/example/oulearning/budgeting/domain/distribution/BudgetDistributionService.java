package com.example.oulearning.budgeting.domain.distribution;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.budgeting.domain.distribution.exception.BudgetDistributionException;
import com.example.oulearning.training.domain.request.vo.identity.OuId;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Domain Service for applying budget distribution strategies across an organizational unit's children.
 */
public final class BudgetDistributionService {

    /**
     * Distributes a parent budget among child organizational units using the specified strategy.
     *
     * @param parentBudget the {@link Budget} of the parent OU
     * @param childOuIds   the set of child {@link OuId}s
     * @param strategy     the {@link BudgetDistributionStrategy} to apply
     * @param idSupplier   the supplier generating unique {@link BudgetId}s for new child budgets
     * @return an unmodifiable {@link List} of child {@link Budget}s
     */
    public List<Budget> distribute(
            Budget parentBudget,
            Set<OuId> childOuIds,
            BudgetDistributionStrategy strategy,
            Supplier<BudgetId> idSupplier) {

        Objects.requireNonNull(parentBudget, "Parent budget cannot be null");
        Objects.requireNonNull(childOuIds, "Child OU IDs cannot be null");
        Objects.requireNonNull(strategy, "Budget distribution strategy cannot be null");
        Objects.requireNonNull(idSupplier, "BudgetId supplier cannot be null");

        return switch (strategy) {
            case BudgetDistributionStrategy.ExclusiveAllocation exclusive -> Collections.emptyList();
            case BudgetDistributionStrategy.EqualDistribution equal ->
                    distributeEqually(parentBudget, childOuIds, idSupplier);
            case BudgetDistributionStrategy.ExplicitDistribution explicit ->
                    distributeExplicitly(parentBudget, childOuIds, explicit, idSupplier);
        };
    }

    private List<Budget> distributeEqually(
            Budget parentBudget, Set<OuId> childOuIds, Supplier<BudgetId> idSupplier) {
        if (childOuIds.isEmpty()) {
            return Collections.emptyList();
        }

        final var count = childOuIds.size();
        final var totalAmount = parentBudget.allocated().amount();
        final var baseAmount = totalAmount.divide(BigDecimal.valueOf(count), 2, RoundingMode.DOWN);
        final var totalDistributed = baseAmount.multiply(BigDecimal.valueOf(count));
        var remainderCents = totalAmount.subtract(totalDistributed)
                .multiply(BigDecimal.valueOf(100))
                .intValue();

        final var result = new ArrayList<Budget>(count);
        final var currency = parentBudget.allocated().currency();

        for (final var childId : childOuIds) {
            var childAmount = baseAmount;
            if (remainderCents > 0) {
                childAmount = childAmount.add(BigDecimal.valueOf(0.01));
                remainderCents--;
            }
            result.add(Budget.of(idSupplier.get(), childId, Money.of(childAmount, currency)));
        }

        return List.copyOf(result);
    }

    private List<Budget> distributeExplicitly(
            Budget parentBudget,
            Set<OuId> childOuIds,
            BudgetDistributionStrategy.ExplicitDistribution explicit,
            Supplier<BudgetId> idSupplier) {

        final var allocations = explicit.allocations();
        final var currency = parentBudget.allocated().currency();

        // Validate that all explicit allocations target known child OUs
        for (final var targetId : allocations.keySet()) {
            if (!childOuIds.contains(targetId)) {
                throw new BudgetDistributionException(
                        "Explicit allocation targets OU '%s' which is not a child of parent OU '%s'"
                                .formatted(targetId, parentBudget.ouId()));
            }
        }

        // Validate total explicit allocation does not exceed parent budget
        final var totalAllocated = allocations.values().stream()
                .reduce(Money.zero(currency), Money::plus);

        if (totalAllocated.compareTo(parentBudget.allocated()) > 0) {
            throw new BudgetDistributionException(
                    "Total explicit allocations (%s) exceed parent allocated budget (%s)"
                            .formatted(totalAllocated, parentBudget.allocated()));
        }

        final var result = new ArrayList<Budget>(childOuIds.size());
        for (final var childId : childOuIds) {
            final var allocatedMoney = allocations.getOrDefault(childId, Money.zero(currency));
            result.add(Budget.of(idSupplier.get(), childId, allocatedMoney));
        }

        return List.copyOf(result);
    }
}
