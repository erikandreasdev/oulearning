package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.budget.Budget;

/**
 * Use case input port for spending directly from allocated budget.
 */
public interface SpendDirectBudgetFundsUseCase {
    Budget execute(SpendDirectCommand command);
}
