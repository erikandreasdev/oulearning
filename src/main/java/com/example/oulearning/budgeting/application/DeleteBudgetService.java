package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.BudgetId;
import com.example.oulearning.budgeting.domain.BudgetRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteBudgetService implements DeleteBudgetUseCase {

    private final BudgetRepository budgetRepository;

    public DeleteBudgetService(final BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Override
    public void execute(final BudgetId id) {
        final var budget = budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException(id));
        budgetRepository.save(budget.deactivate());
    }
}
