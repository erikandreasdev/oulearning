package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.Budget;
import com.example.oulearning.budgeting.domain.BudgetId;
import com.example.oulearning.budgeting.domain.BudgetRepository;
import org.springframework.stereotype.Service;

@Service
public class GetBudgetService implements GetBudgetUseCase {

    private final BudgetRepository budgetRepository;

    public GetBudgetService(final BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Override
    public Budget execute(final BudgetId id) {
        return budgetRepository.findById(id)
                .orElseThrow(() -> new BudgetNotFoundException(id));
    }
}
