package com.example.oulearning.budgeting.application.port.in.usecase.query;

import com.example.oulearning.budgeting.domain.budget.Budget;
import java.util.Optional;
import com.example.oulearning.budgeting.application.port.in.query.GetBudgetQuery;

/**
 * Use case input port for retrieving a Budget.
 */
public interface GetBudgetUseCase {
    Optional<Budget> execute(GetBudgetQuery query);
}
