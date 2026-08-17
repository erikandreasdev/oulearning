package com.example.oulearning.budgeting.domain.distribution;

import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.budgeting.domain.budget.exception.InvalidBudgetException;
import com.example.oulearning.organization.domain.unit.OuId;
import java.util.Map;

/**
 * Sealed interface for strategies defining how a parent organizational unit's budget is distributed to its children.
 */
public sealed interface BudgetDistributionStrategy
        permits BudgetDistributionStrategy.ExclusiveAllocation,
                BudgetDistributionStrategy.EqualDistribution,
                BudgetDistributionStrategy.ExplicitDistribution {

    record ExclusiveAllocation() implements BudgetDistributionStrategy {}

    record EqualDistribution() implements BudgetDistributionStrategy {}

    record ExplicitDistribution(Map<OuId, Money> allocations) implements BudgetDistributionStrategy {

        public ExplicitDistribution {
            if (allocations == null) {
                throw new InvalidBudgetException("Explicit allocations map cannot be null");
            }
            allocations = Map.copyOf(allocations);
        }

        public static ExplicitDistribution of(Map<OuId, Money> allocations) {
            return new ExplicitDistribution(allocations);
        }
    }
}
