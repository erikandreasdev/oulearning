package com.example.oulearning.budgeting.application.port.in;

import com.example.oulearning.budgeting.domain.model.BudgetId;

public interface CreateBudgetUseCase {
    BudgetId execute(CreateBudgetCommand command);
}
