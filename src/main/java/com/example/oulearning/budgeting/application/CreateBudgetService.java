package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.Budget;
import com.example.oulearning.budgeting.domain.BudgetId;
import com.example.oulearning.budgeting.domain.BudgetRepository;
import com.example.oulearning.budgeting.domain.FiscalYear;
import com.example.oulearning.budgeting.domain.IdGenerator;
import com.example.oulearning.budgeting.domain.Money;
import org.springframework.stereotype.Service;

@Service
public class CreateBudgetService implements CreateBudgetUseCase {

    private final BudgetRepository budgetRepository;
    private final IdGenerator idGenerator;

    public CreateBudgetService(final BudgetRepository budgetRepository, final IdGenerator idGenerator) {
        this.budgetRepository = budgetRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public BudgetId execute(final CreateBudgetCommand command) {
        final var id = BudgetId.of(idGenerator.generate());
        final var budget = Budget.create(
                id,
                command.ouId(),
                FiscalYear.of(command.fiscalYear()),
                Money.of(command.total()),
                Money.of(command.reserved()),
                Money.of(command.available()));
        budgetRepository.save(budget);
        return id;
    }
}
