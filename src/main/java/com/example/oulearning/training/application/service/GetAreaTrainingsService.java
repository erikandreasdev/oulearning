package com.example.oulearning.training.application.service;

import com.example.oulearning.budgeting.domain.repository.BudgetRepository;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.application.port.in.model.AreaTrainingItemDto;
import com.example.oulearning.training.application.port.in.model.AreaTrainingsOverviewDto;
import com.example.oulearning.training.application.port.in.usecase.GetAreaTrainingsUseCase;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetAreaTrainingsService implements GetAreaTrainingsUseCase {

    private final TrainingRepository trainingRepository;
    private final BudgetRepository budgetRepository;

    public GetAreaTrainingsService(
            final TrainingRepository trainingRepository,
            final BudgetRepository budgetRepository) {
        this.trainingRepository = trainingRepository;
        this.budgetRepository = budgetRepository;
    }

    @Override
    public AreaTrainingsOverviewDto execute(final OrganizationalUnitId organizationalUnitId) {
        final var budgets = budgetRepository.findByOrganizationalUnitId(organizationalUnitId);
        final var assignedBudget = budgets.stream()
                .map(b -> b.total().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        final var availableBudget = budgets.stream()
                .map(b -> b.available().amount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        final var trainings = trainingRepository.findByOrganizationalUnitId(organizationalUnitId);
        final var items = trainings.stream()
                .map(t -> new AreaTrainingItemDto(
                        t.id(),
                        t.name(),
                        List.of(t.organizationalUnitId()),
                        t.cost(),
                        t.status()))
                .toList();

        return new AreaTrainingsOverviewDto(assignedBudget, availableBudget, items);
    }
}
