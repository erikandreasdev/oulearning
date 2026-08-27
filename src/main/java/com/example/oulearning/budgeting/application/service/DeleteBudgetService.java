package com.example.oulearning.budgeting.application.service;

import com.example.oulearning.budgeting.application.exception.BudgetNotFoundException;
import com.example.oulearning.budgeting.application.port.in.usecase.DeleteBudgetUseCase;
import com.example.oulearning.budgeting.domain.model.BudgetId;
import com.example.oulearning.budgeting.domain.repository.BudgetRepository;
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
