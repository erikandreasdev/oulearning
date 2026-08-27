package com.example.oulearning.budgeting.infrastructure.web;

import com.example.oulearning.budgeting.application.port.in.CreateBudgetCommand;
import com.example.oulearning.budgeting.application.port.in.CreateBudgetUseCase;
import com.example.oulearning.budgeting.application.port.in.DeleteBudgetUseCase;
import com.example.oulearning.budgeting.application.port.in.GetBudgetUseCase;
import com.example.oulearning.budgeting.application.port.in.UpdateBudgetCommand;
import com.example.oulearning.budgeting.application.port.in.UpdateBudgetUseCase;
import com.example.oulearning.budgeting.domain.model.Budget;
import com.example.oulearning.budgeting.domain.model.BudgetId;
import com.example.oulearning.budgeting.infrastructure.web.api.BudgetsApi;
import com.example.oulearning.budgeting.infrastructure.web.dto.BudgetResponse;
import com.example.oulearning.budgeting.infrastructure.web.dto.CreateBudgetRequest;
import com.example.oulearning.budgeting.infrastructure.web.dto.UpdateBudgetRequest;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class BudgetController implements BudgetsApi {

    private final CreateBudgetUseCase createBudgetUseCase;
    private final GetBudgetUseCase getBudgetUseCase;
    private final UpdateBudgetUseCase updateBudgetUseCase;
    private final DeleteBudgetUseCase deleteBudgetUseCase;

    BudgetController(
            final CreateBudgetUseCase createBudgetUseCase,
            final GetBudgetUseCase getBudgetUseCase,
            final UpdateBudgetUseCase updateBudgetUseCase,
            final DeleteBudgetUseCase deleteBudgetUseCase) {
        this.createBudgetUseCase = createBudgetUseCase;
        this.getBudgetUseCase = getBudgetUseCase;
        this.updateBudgetUseCase = updateBudgetUseCase;
        this.deleteBudgetUseCase = deleteBudgetUseCase;
    }

    @Override
    public ResponseEntity<BudgetResponse> createBudget(final CreateBudgetRequest request) {
        final var command = new CreateBudgetCommand(
                new OrganizationalUnitId(request.getOrganizationalUnitId()),
                request.getFiscalYear(),
                request.getTotalAmount(),
                request.getReservedAmount(),
                request.getAvailableAmount());
        final var id = createBudgetUseCase.execute(command);
        final var budget = getBudgetUseCase.execute(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(budget));
    }

    @Override
    public ResponseEntity<BudgetResponse> getBudget(final Long id) {
        final var budget = getBudgetUseCase.execute(new BudgetId(id));
        return ResponseEntity.ok(toResponse(budget));
    }

    @Override
    public ResponseEntity<BudgetResponse> updateBudget(final Long id, final UpdateBudgetRequest request) {
        final var command = new UpdateBudgetCommand(
                new BudgetId(id), request.getTotalAmount(), request.getReservedAmount(), request.getAvailableAmount());
        updateBudgetUseCase.execute(command);
        final var budget = getBudgetUseCase.execute(new BudgetId(id));
        return ResponseEntity.ok(toResponse(budget));
    }

    @Override
    public ResponseEntity<Void> deleteBudget(final Long id) {
        deleteBudgetUseCase.execute(new BudgetId(id));
        return ResponseEntity.noContent().build();
    }

    private BudgetResponse toResponse(final Budget budget) {
        final var response = new BudgetResponse();
        response.setId(budget.id().value());
        response.setOrganizationalUnitId(budget.organizationalUnitId().value());
        response.setFiscalYear(budget.fiscalYear().value());
        response.setTotalAmount(budget.total().amount());
        response.setReservedAmount(budget.reserved().amount());
        response.setAvailableAmount(budget.available().amount());
        response.setActive(budget.active());
        return response;
    }
}
