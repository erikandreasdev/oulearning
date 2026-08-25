package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.BudgetRepository;
import com.example.oulearning.budgeting.domain.Money;
import org.springframework.stereotype.Service;

@Service
public class UpdateBudgetService implements UpdateBudgetUseCase {

    private final BudgetRepository budgetRepository;

    public UpdateBudgetService(final BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @Override
    public void execute(final UpdateBudgetCommand command) {
        final var budget = budgetRepository.findById(command.id())
                .orElseThrow(() -> new BudgetNotFoundException(command.id()));
        final var updated = budget.updateAmounts(
                Money.of(command.total()),
                Money.of(command.reserved()),
                Money.of(command.available()));
        budgetRepository.save(updated);
    }
}
