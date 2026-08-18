package com.example.oulearning.budgeting.application.port.in.usecase.funds;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.application.port.in.command.ReleaseFundsCommand;

/**
 * Use case input port for releasing reserved funds from a budget.
 */
public interface ReleaseBudgetFundsUseCase {
    Budget execute(ReleaseFundsCommand command);
}
