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
 * Use case input port for reserving funds from a budget.
 */
public interface ReserveBudgetFundsUseCase {
    Budget execute(ReserveFundsCommand command);
}
