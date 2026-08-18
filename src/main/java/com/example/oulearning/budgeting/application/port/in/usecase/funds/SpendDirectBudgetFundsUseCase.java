package com.example.oulearning.budgeting.application.port.in.usecase.funds;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.application.port.in.command.SpendDirectCommand;

/**
 * Use case input port for spending directly from allocated budget.
 */
public interface SpendDirectBudgetFundsUseCase {
    Budget execute(SpendDirectCommand command);
}
