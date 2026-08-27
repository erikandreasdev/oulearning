package com.example.oulearning.budgeting.application.service;

import com.example.oulearning.budgeting.application.port.in.command.CreateBudgetCommand;
import com.example.oulearning.budgeting.application.port.in.usecase.CreateBudgetUseCase;
import com.example.oulearning.budgeting.domain.model.Budget;
import com.example.oulearning.budgeting.domain.model.BudgetId;
import com.example.oulearning.budgeting.domain.model.FiscalYear;
import com.example.oulearning.budgeting.domain.model.IdGenerator;
import com.example.oulearning.budgeting.domain.model.Money;
import com.example.oulearning.budgeting.domain.repository.BudgetRepository;
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
