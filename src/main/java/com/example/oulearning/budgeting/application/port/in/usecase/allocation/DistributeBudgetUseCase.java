package com.example.oulearning.budgeting.application.port.in.usecase.allocation;

import com.example.oulearning.budgeting.application.dto.BudgetDistributionResult;
import com.example.oulearning.budgeting.application.port.in.command.DistributeBudgetCommand;
/**
 * Use case input port for orchestrating budget distribution among child OUs.
 */
public interface DistributeBudgetUseCase {
    BudgetDistributionResult execute(DistributeBudgetCommand command);
}
