package com.example.oulearning.budgeting.application.service;

import com.example.oulearning.budgeting.application.port.in.*;
import com.example.oulearning.budgeting.application.exception.*;

import com.example.oulearning.budgeting.domain.model.Budget;
import com.example.oulearning.budgeting.domain.model.BudgetId;
import com.example.oulearning.budgeting.domain.repository.BudgetRepository;
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
