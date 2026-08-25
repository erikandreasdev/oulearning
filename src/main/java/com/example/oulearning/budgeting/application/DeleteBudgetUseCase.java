package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.BudgetId;

public interface DeleteBudgetUseCase {
    void execute(BudgetId id);
}
