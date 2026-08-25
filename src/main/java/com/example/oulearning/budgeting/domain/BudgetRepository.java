package com.example.oulearning.budgeting.domain;

import java.util.Optional;

public interface BudgetRepository {
    Optional<Budget> findById(BudgetId id);

    void save(Budget budget);
}
