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
 * Service orchestrating consumption of reserved funds.
 */
@Service
@Transactional
public class ConsumeBudgetFundsService implements ConsumeBudgetFundsUseCase {

    private final BudgetRepository repository;

    public ConsumeBudgetFundsService(BudgetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "BudgetRepository cannot be null");
    }

    @Override
    public Budget execute(ConsumeFundsCommand command) {
        Objects.requireNonNull(command, "ConsumeFundsCommand cannot be null");

        final var budgetId = BudgetId.of(command.budgetId());
        final var budget = repository.findById(budgetId)
                .orElseThrow(() -> new NoSuchElementException("Budget '%s' not found".formatted(command.budgetId())));

        final var currency = command.currencyCode() != null
                ? Monetary.getCurrency(command.currencyCode())
                : budget.allocated().currency();
        final var amountToConsume = Money.of(command.amount(), currency);

        final var updatedBudget = budget.consumeReserved(amountToConsume);
        repository.save(updatedBudget);

        return updatedBudget;
    }
}
