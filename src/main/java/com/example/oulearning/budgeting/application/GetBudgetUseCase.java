package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.Budget;
import com.example.oulearning.budgeting.domain.BudgetId;

public interface GetBudgetUseCase {
    Budget execute(BudgetId id);
}
