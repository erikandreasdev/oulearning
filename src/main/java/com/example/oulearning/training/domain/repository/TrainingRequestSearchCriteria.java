package com.example.oulearning.training.domain.repository;

import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.training.domain.OuId;
import com.example.oulearning.training.domain.TrainingRequestStatus;
import java.util.Collection;
import java.util.Set;

/**
 * Value criteria for filtering and querying training requests.
 */
public record TrainingRequestSearchCriteria(
        Set<OuId> ouIds,
        TrainingRequestStatus status,
        FiscalYear fiscalYear) {

    public TrainingRequestSearchCriteria {
        ouIds = ouIds != null ? Set.copyOf(ouIds) : Set.of();
    }

    public static TrainingRequestSearchCriteria of(
            Collection<OuId> ouIds,
            TrainingRequestStatus status,
            FiscalYear fiscalYear) {
        return new TrainingRequestSearchCriteria(
                ouIds != null ? Set.copyOf(ouIds) : Set.of(),
                status,
                fiscalYear);
    }

    public static TrainingRequestSearchCriteria empty() {
        return new TrainingRequestSearchCriteria(Set.of(), null, null);
    }
}
