package com.example.oulearning.budgeting.application.port.in;

import com.example.oulearning.budgeting.domain.model.Budget;
import com.example.oulearning.budgeting.domain.model.BudgetId;

public interface GetBudgetUseCase {
    Budget execute(BudgetId id);
}
