package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.budget.Budget;
import java.util.Optional;

/**
 * Use case input port for retrieving a Budget.
 */
public interface GetBudgetUseCase {
    Optional<Budget> execute(GetBudgetQuery query);
}
