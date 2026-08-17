package com.example.oulearning.budgeting.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Immutable application command for distributing parent OU budget to child OUs.
 */
public record DistributeBudgetCommand(
        UUID parentOuId,
        String strategyType,
        List<UUID> childOuIds,
        Map<UUID, BigDecimal> explicitAllocations,
        String currencyCode) {

    public DistributeBudgetCommand {
        childOuIds = childOuIds != null ? List.copyOf(childOuIds) : List.of();
        explicitAllocations = explicitAllocations != null ? Map.copyOf(explicitAllocations) : Map.of();
    }
}
