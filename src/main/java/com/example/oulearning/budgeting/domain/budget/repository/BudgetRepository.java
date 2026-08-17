package com.example.oulearning.budgeting.domain.budget.repository;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.organization.domain.unit.OuId;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for persisting and querying {@link Budget} aggregates.
 */
public interface BudgetRepository {

    void save(Budget budget);

    Optional<Budget> findById(BudgetId id);

    Optional<Budget> findByOuId(OuId ouId);

    List<Budget> findAllByOuIds(Collection<OuId> ouIds);
}
