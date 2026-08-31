package com.example.oulearning.budgeting.infrastructure.web;

import com.example.oulearning.budgeting.application.port.in.command.CreateOrganizationalUnitBudgetsCommand;
import com.example.oulearning.budgeting.application.port.in.model.OrganizationalUnitBudgetDto;
import com.example.oulearning.budgeting.application.port.in.usecase.CreateOrganizationalUnitBudgetsUseCase;
import com.example.oulearning.budgeting.application.port.in.usecase.GetBudgetsByOrganizationalUnitUseCase;
import com.example.oulearning.budgeting.infrastructure.web.api.BudgetsApi;
import com.example.oulearning.budgeting.infrastructure.web.dto.CreateOuBudgetRequest;
import com.example.oulearning.budgeting.infrastructure.web.dto.OuBudgetResponse;
import com.example.oulearning.budgeting.infrastructure.web.dto.PaginatedOuBudgetResponse;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class BudgetController implements BudgetsApi {

    private final GetBudgetsByOrganizationalUnitUseCase getBudgetsByOrganizationalUnitUseCase;
    private final CreateOrganizationalUnitBudgetsUseCase createOrganizationalUnitBudgetsUseCase;

    BudgetController(
            final GetBudgetsByOrganizationalUnitUseCase getBudgetsByOrganizationalUnitUseCase,
            final CreateOrganizationalUnitBudgetsUseCase createOrganizationalUnitBudgetsUseCase) {
        this.getBudgetsByOrganizationalUnitUseCase = getBudgetsByOrganizationalUnitUseCase;
        this.createOrganizationalUnitBudgetsUseCase = createOrganizationalUnitBudgetsUseCase;
    }

    @Override
    public ResponseEntity<List<OuBudgetResponse>> getBudgetsByOrganizationalUnit(
            final Long organizationalUnitId, final Boolean includeSubtree) {
        final var dtos = getBudgetsByOrganizationalUnitUseCase.execute(
                new OrganizationalUnitId(organizationalUnitId),
                Boolean.TRUE.equals(includeSubtree));
        final var responses = dtos.stream().map(this::toResponse).toList();
        return ResponseEntity.ok(responses);
    }

    @Override
    public ResponseEntity<PaginatedOuBudgetResponse> createOrganizationalUnitBudgets(
            final CreateOuBudgetRequest request) {
        final Set<OrganizationalUnitId> targetChildOuIds = request.getTargetChildOuIds() != null
                ? request.getTargetChildOuIds().stream().map(OrganizationalUnitId::new).collect(Collectors.toSet())
                : Set.of();

        final Set<EmployeeId> owners = request.getOwners() != null
                ? request.getOwners().stream().map(EmployeeId::new).collect(Collectors.toSet())
                : Set.of();

        final var command = new CreateOrganizationalUnitBudgetsCommand(
                request.getAssignedBudget(),
                request.getFiscalYear(),
                new OrganizationalUnitId(request.getOrganizationalUnitId()),
                owners,
                request.getIncludeAllChildren(),
                targetChildOuIds,
                request.getPage(),
                request.getSize());

        final var result = createOrganizationalUnitBudgetsUseCase.execute(command);
        final var response = new PaginatedOuBudgetResponse();
        response.setItems(result.items().stream().map(this::toResponse).toList());
        response.setTotalElements(result.totalElements());
        response.setTotalPages(result.totalPages());
        response.setPage(result.page());
        response.setSize(result.size());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private OuBudgetResponse toResponse(final OrganizationalUnitBudgetDto dto) {
        final var response = new OuBudgetResponse();
        response.setId(dto.id().value());
        response.setOrganizationalUnitId(dto.organizationalUnitId().value());
        response.setAssignedBudget(dto.total().amount());
        response.setAvailableBudget(dto.available().amount());
        response.setReservedBudget(dto.reserved().amount());
        response.setFiscalYear(dto.fiscalYear().value());
        response.setOwners(dto.owners().stream().map(EmployeeId::value).toList());
        return response;
    }
}
