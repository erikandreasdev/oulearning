package com.example.oulearning.budgeting.application.port.in.usecase;

import com.example.oulearning.budgeting.domain.model.BudgetId;

public interface DeleteBudgetUseCase {
    void execute(BudgetId id);
}
