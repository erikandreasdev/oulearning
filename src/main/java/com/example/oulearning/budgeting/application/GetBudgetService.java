package com.example.oulearning.budgeting.application;

import com.example.oulearning.budgeting.domain.budget.Budget;
import com.example.oulearning.budgeting.domain.budget.BudgetId;
import com.example.oulearning.budgeting.domain.budget.repository.BudgetRepository;
import com.example.oulearning.organization.domain.unit.OuId;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating budget queries.
 */
@Service
@Transactional(readOnly = true)
public class GetBudgetService implements GetBudgetUseCase {

    private final BudgetRepository repository;

    public GetBudgetService(BudgetRepository repository) {
        this.repository = Objects.requireNonNull(repository, "BudgetRepository cannot be null");
    }

    @Override
    public Optional<Budget> execute(GetBudgetQuery query) {
        Objects.requireNonNull(query, "GetBudgetQuery cannot be null");

        if (query.budgetId() != null) {
            return repository.findById(BudgetId.of(query.budgetId()));
        } else if (query.ouId() != null) {
            return repository.findByOuId(OuId.of(query.ouId()));
        }

        return Optional.empty();
    }
}
