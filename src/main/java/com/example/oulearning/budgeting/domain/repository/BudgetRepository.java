package com.example.oulearning.budgeting.domain.repository;

import com.example.oulearning.budgeting.domain.model.Budget;
import com.example.oulearning.budgeting.domain.model.BudgetId;

import java.util.Optional;

public interface BudgetRepository {
    Optional<Budget> findById(BudgetId id);

    void save(Budget budget);
}
