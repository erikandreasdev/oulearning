package com.example.oulearning.budgeting.domain;

import com.example.oulearning.shared.domain.OuId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for persisting and querying {@link Budget} aggregates.
 */
public interface BudgetRepository {

    /**
     * Persists or updates a {@link Budget} aggregate.
     *
     * @param budget the budget to save
     */
    void save(Budget budget);

    /**
     * Finds a budget by its unique {@link BudgetId}.
     *
     * @param id the budget ID
     * @return an {@link Optional} containing the budget if found, or empty
     */
    Optional<Budget> findById(BudgetId id);

    /**
     * Finds the budget assigned to a specific organizational unit.
     *
     * @param ouId the organizational unit ID
     * @return an {@link Optional} containing the budget if found, or empty
     */
    Optional<Budget> findByOuId(OuId ouId);

    /**
     * Finds all budgets assigned to a collection of organizational units.
     *
     * @param ouIds the collection of OU IDs
     * @return an unmodifiable {@link List} of matching budgets
     */
    List<Budget> findAllByOuIds(Collection<OuId> ouIds);
}
