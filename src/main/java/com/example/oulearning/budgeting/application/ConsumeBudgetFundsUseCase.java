package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.budget.Budget;

/**
 * Use case input port for consuming reserved funds from a budget.
 */
public interface ConsumeBudgetFundsUseCase {
    Budget execute(ConsumeFundsCommand command);
}
