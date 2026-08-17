package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.budget.Budget;

/**
 * Use case input port for releasing reserved funds from a budget.
 */
public interface ReleaseBudgetFundsUseCase {
    Budget execute(ReleaseFundsCommand command);
}
