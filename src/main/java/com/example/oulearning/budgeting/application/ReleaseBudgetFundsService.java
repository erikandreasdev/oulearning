package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.Money;
import com.example.oulearning.budgeting.domain.budget.repository.BudgetRepository;
import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import java.time.Clock;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.money.Monetary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating budget release.
 */
@Service
@Transactional
public class ReleaseBudgetFundsService implements ReleaseBudgetFundsUseCase {

    private final BudgetRepository repository;
    private final Clock clock;

    public ReleaseBudgetFundsService(BudgetRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "BudgetRepository cannot be null");
        this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
    }

    @Override
    public Budget execute(ReleaseFundsCommand command) {
        Objects.requireNonNull(command, "ReleaseFundsCommand cannot be null");

        final var budgetId = BudgetId.of(command.budgetId());
        final var budget = repository.findById(budgetId)
                .orElseThrow(() -> new NoSuchElementException("Budget '%s' not found".formatted(command.budgetId())));

        final var currency = command.currencyCode() != null
                ? Monetary.getCurrency(command.currencyCode())
                : budget.allocated().currency();
        final var amountToRelease = Money.of(command.amount(), currency);

        final var updatedBudget = budget.releaseReservation(amountToRelease, FiscalYear.current(clock));
        repository.save(updatedBudget);

        return updatedBudget;
    }
}
