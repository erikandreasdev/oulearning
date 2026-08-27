package com.example.oulearning.budgeting.application.port.in.usecase;

import com.example.oulearning.budgeting.application.port.in.command.CreateBudgetCommand;
import com.example.oulearning.budgeting.domain.model.BudgetId;

public interface CreateBudgetUseCase {
    BudgetId execute(CreateBudgetCommand command);
}
