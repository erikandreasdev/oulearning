package com.example.oulearning.budgeting.application.port.in.usecase.funds;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.application.port.in.command.ConsumeFundsCommand;

/**
 * Use case input port for consuming reserved funds from a budget.
 */
public interface ConsumeBudgetFundsUseCase {
    Budget execute(ConsumeFundsCommand command);
}
