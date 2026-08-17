package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.budgeting.domain.budget.repository.BudgetRepository;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.money.Monetary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating budget reservation.
 */
@Service
@Transactional
public class ReserveBudgetFundsService implements ReserveBudgetFundsUseCase {

    private final BudgetRepository repository;

    public ReserveBudgetFundsService(BudgetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "BudgetRepository cannot be null");
    }

    @Override
    public Budget execute(ReserveFundsCommand command) {
        Objects.requireNonNull(command, "ReserveFundsCommand cannot be null");

        final var budgetId = BudgetId.of(command.budgetId());
        final var budget = repository.findById(budgetId)
                .orElseThrow(() -> new NoSuchElementException("Budget '%s' not found".formatted(command.budgetId())));

        final var currency = command.currencyCode() != null
                ? Monetary.getCurrency(command.currencyCode())
                : budget.allocated().currency();
        final var amountToReserve = Money.of(command.amount(), currency);

        final var updatedBudget = budget.reserve(amountToReserve);
        repository.save(updatedBudget);

        return updatedBudget;
    }
}
