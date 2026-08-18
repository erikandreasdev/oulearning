package com.example.oulearning.budgeting.domain.budget.repository;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.training.domain.request.vo.identity.OuId;
import com.example.oulearning.shared.domain.fiscal.FiscalYear;
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

    Optional<Budget> findByOuIdAndFiscalYear(OuId ouId, FiscalYear fiscalYear);

    List<Budget> findAllByOuIds(Collection<OuId> ouIds);

    List<Budget> findAllByOuIdsAndFiscalYear(Collection<OuId> ouIds, FiscalYear fiscalYear);
}
