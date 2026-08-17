package com.example.oulearning.budgeting.domain;

import com.example.oulearning.shared.domain.Money;
import com.example.oulearning.shared.domain.OuId;
import java.util.Map;

/**
 * Sealed interface for strategies defining how a parent organizational unit's budget is distributed to its children.
 */
public sealed interface BudgetDistributionStrategy
        permits BudgetDistributionStrategy.ExclusiveAllocation,
                BudgetDistributionStrategy.EqualDistribution,
                BudgetDistributionStrategy.ExplicitDistribution {

    /**
     * Strategy where the budget is assigned exclusively to the OU without cascading to child OUs.
     */
    record ExclusiveAllocation() implements BudgetDistributionStrategy {}

    /**
     * Strategy where the parent's budget is distributed equally among all child OUs.
     */
    record EqualDistribution() implements BudgetDistributionStrategy {}

    /**
     * Strategy where explicit custom amounts are allocated to specific child OUs.
     *
     * @param allocations the map of child {@link OuId} to allocated {@link Money}
     */
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
