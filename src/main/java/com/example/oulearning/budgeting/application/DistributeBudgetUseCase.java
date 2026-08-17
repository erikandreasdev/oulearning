package com.example.oulearning.budgeting.application;

/**
 * Use case input port for orchestrating budget distribution among child OUs.
 */
public interface DistributeBudgetUseCase {
    BudgetDistributionResult execute(DistributeBudgetCommand command);
}
