package com.example.oulearning.budgeting.application.service;

import com.example.oulearning.budgeting.application.port.in.command.CreateOrganizationalUnitBudgetsCommand;
import com.example.oulearning.budgeting.application.port.in.model.OrganizationalUnitBudgetDto;
import com.example.oulearning.budgeting.application.port.in.model.PaginatedBudgetsResult;
import com.example.oulearning.budgeting.application.port.in.usecase.CreateOrganizationalUnitBudgetsUseCase;
import com.example.oulearning.budgeting.domain.model.Budget;
import com.example.oulearning.budgeting.domain.model.BudgetId;
import com.example.oulearning.budgeting.domain.model.FiscalYear;
import com.example.oulearning.budgeting.domain.model.IdGenerator;
import com.example.oulearning.budgeting.domain.model.Money;
import com.example.oulearning.budgeting.domain.repository.BudgetRepository;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetSubtreeOrganizationalUnitsUseCase;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CreateOrganizationalUnitBudgetsService implements CreateOrganizationalUnitBudgetsUseCase {

    private final BudgetRepository budgetRepository;
    private final IdGenerator idGenerator;
    private final GetOrganizationalUnitUseCase getOrganizationalUnitUseCase;
    private final GetSubtreeOrganizationalUnitsUseCase getSubtreeOrganizationalUnitsUseCase;

    public CreateOrganizationalUnitBudgetsService(
            final BudgetRepository budgetRepository,
            final IdGenerator idGenerator,
            final GetOrganizationalUnitUseCase getOrganizationalUnitUseCase,
            final GetSubtreeOrganizationalUnitsUseCase getSubtreeOrganizationalUnitsUseCase) {
        this.budgetRepository = budgetRepository;
        this.idGenerator = idGenerator;
        this.getOrganizationalUnitUseCase = getOrganizationalUnitUseCase;
        this.getSubtreeOrganizationalUnitsUseCase = getSubtreeOrganizationalUnitsUseCase;
    }

    @Override
    public PaginatedBudgetsResult execute(final CreateOrganizationalUnitBudgetsCommand command) {
        final List<OrganizationalUnit> targetUnits;
        if (Boolean.TRUE.equals(command.includeAllChildren())) {
            targetUnits = getSubtreeOrganizationalUnitsUseCase.execute(command.organizationalUnitId());
        } else if (command.targetChildOuIds() != null && !command.targetChildOuIds().isEmpty()) {
            final var subtree = getSubtreeOrganizationalUnitsUseCase.execute(command.organizationalUnitId());
            targetUnits = subtree.stream()
                    .filter(ou -> ou.id().equals(command.organizationalUnitId())
                            || command.targetChildOuIds().contains(ou.id()))
                    .toList();
        } else {
            targetUnits = List.of(getOrganizationalUnitUseCase.execute(command.organizationalUnitId()));
        }

        final var createdBudgets = new ArrayList<OrganizationalUnitBudgetDto>();
        for (final var unit : targetUnits) {
            final var id = BudgetId.of(idGenerator.generate());
            final var budget = Budget.create(
                    id,
                    unit.id(),
                    FiscalYear.of(command.fiscalYear()),
                    Money.of(command.assignedBudget()),
                    Money.zero(),
                    Money.of(command.assignedBudget()));
            budgetRepository.save(budget);
            createdBudgets.add(new OrganizationalUnitBudgetDto(
                    budget.id(),
                    budget.organizationalUnitId(),
                    budget.total(),
                    budget.available(),
                    budget.reserved(),
                    budget.fiscalYear(),
                    unit.owners().stream().toList()));
        }

        final int page = command.page() != null && command.page() >= 0 ? command.page() : 0;
        final int size = command.size() != null && command.size() > 0 ? command.size() : 20;
        final long totalElements = createdBudgets.size();
        final int totalPages = (int) Math.ceil((double) totalElements / size);

        final int fromIndex = Math.min(page * size, createdBudgets.size());
        final int toIndex = Math.min(fromIndex + size, createdBudgets.size());
        final var pagedItems = createdBudgets.subList(fromIndex, toIndex);

        return new PaginatedBudgetsResult(pagedItems, totalElements, totalPages, page, size);
    }
}
