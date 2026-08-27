package com.example.oulearning.budgeting.application.service;

import com.example.oulearning.budgeting.application.port.in.model.OrganizationalUnitBudgetDto;
import com.example.oulearning.budgeting.application.port.in.usecase.GetBudgetsByOrganizationalUnitUseCase;
import com.example.oulearning.budgeting.domain.repository.BudgetRepository;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetSubtreeOrganizationalUnitsUseCase;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetBudgetsByOrganizationalUnitService implements GetBudgetsByOrganizationalUnitUseCase {

    private final BudgetRepository budgetRepository;
    private final GetOrganizationalUnitUseCase getOrganizationalUnitUseCase;
    private final GetSubtreeOrganizationalUnitsUseCase getSubtreeOrganizationalUnitsUseCase;

    public GetBudgetsByOrganizationalUnitService(
            final BudgetRepository budgetRepository,
            final GetOrganizationalUnitUseCase getOrganizationalUnitUseCase,
            final GetSubtreeOrganizationalUnitsUseCase getSubtreeOrganizationalUnitsUseCase) {
        this.budgetRepository = budgetRepository;
        this.getOrganizationalUnitUseCase = getOrganizationalUnitUseCase;
        this.getSubtreeOrganizationalUnitsUseCase = getSubtreeOrganizationalUnitsUseCase;
    }

    @Override
    public List<OrganizationalUnitBudgetDto> execute(
            final OrganizationalUnitId organizationalUnitId,
            final boolean includeSubtree) {
        final List<OrganizationalUnit> units;
        if (includeSubtree) {
            units = getSubtreeOrganizationalUnitsUseCase.execute(organizationalUnitId);
        } else {
            units = List.of(getOrganizationalUnitUseCase.execute(organizationalUnitId));
        }

        final var result = new ArrayList<OrganizationalUnitBudgetDto>();
        for (final var unit : units) {
            final var budgets = budgetRepository.findByOrganizationalUnitId(unit.id());
            final var ownersList = unit.owners().stream().toList();
            for (final var budget : budgets) {
                result.add(new OrganizationalUnitBudgetDto(
                        budget.id(),
                        budget.organizationalUnitId(),
                        budget.total(),
                        budget.available(),
                        budget.reserved(),
                        budget.fiscalYear(),
                        ownersList));
            }
        }
        return List.copyOf(result);
    }
}
