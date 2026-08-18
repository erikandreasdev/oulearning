package com.example.oulearning.training.application.port.in.query;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Immutable query to search training requests by multiple criteria.
 */
public record GetTrainingRequestsQuery(
        Set<UUID> ouIds,
        Set<String> ouNames,
        String status,
        Integer fiscalYear) {

    public GetTrainingRequestsQuery {
        ouIds = ouIds != null ? Set.copyOf(ouIds) : Set.of();
        ouNames = ouNames != null ? Set.copyOf(ouNames) : Set.of();
    }

    public static GetTrainingRequestsQuery of(
            Collection<UUID> ouIds,
            Collection<String> ouNames,
            String status,
            Integer fiscalYear) {
        return new GetTrainingRequestsQuery(
                ouIds != null ? Set.copyOf(ouIds) : Set.of(),
                ouNames != null ? Set.copyOf(ouNames) : Set.of(),
                status,
                fiscalYear);
    }
}
