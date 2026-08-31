package com.example.oulearning.budgeting.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.budgeting.application.port.in.command.CreateOrganizationalUnitBudgetsCommand;
import com.example.oulearning.budgeting.domain.model.Budget;
import com.example.oulearning.budgeting.domain.model.BudgetingTestFactory;
import com.example.oulearning.budgeting.domain.model.IdGenerator;
import com.example.oulearning.budgeting.domain.repository.BudgetRepository;
import com.example.oulearning.organization.application.hierarchy.port.in.command.AssignOwnerCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.AssignOwnerUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetSubtreeOrganizationalUnitsUseCase;
import com.example.oulearning.organization.domain.employee.model.EmployeeTestFactory;
import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateOrganizationalUnitBudgetsServiceTest {

    private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
    private final IdGenerator idGenerator = mock(IdGenerator.class);
    private final GetOrganizationalUnitUseCase getOrganizationalUnitUseCase = mock(GetOrganizationalUnitUseCase.class);
    private final GetSubtreeOrganizationalUnitsUseCase getSubtreeOrganizationalUnitsUseCase =
            mock(GetSubtreeOrganizationalUnitsUseCase.class);
    private final AssignOwnerUseCase assignOwnerUseCase = mock(AssignOwnerUseCase.class);
    private final CreateOrganizationalUnitBudgetsService service = new CreateOrganizationalUnitBudgetsService(
            budgetRepository,
            idGenerator,
            getOrganizationalUnitUseCase,
            getSubtreeOrganizationalUnitsUseCase,
            assignOwnerUseCase);

    @Test
    @DisplayName("given single OU command, when creating budgets, then save single budget and return result")
    void givenSingleOuCommand_whenCreatingBudgets_thenSaveSingleBudgetAndReturnResult() {
        // given
        final var ou = HierarchyTestFactory.randomOrganizationalUnit();
        final var owner = EmployeeTestFactory.randomEmployeeId();
        final var amount = BudgetingTestFactory.randomBigDecimalAmount();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var command = new CreateOrganizationalUnitBudgetsCommand(
                amount, fiscalYear, ou.id(), Set.of(owner), false, Set.of(), 0, 20);
        when(getOrganizationalUnitUseCase.execute(ou.id())).thenReturn(ou);
        when(idGenerator.generate()).thenReturn(100L);

        // when
        final var result = service.execute(command);

        // then
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.items()).hasSize(1);
        verify(assignOwnerUseCase, times(1)).execute(any(AssignOwnerCommand.class));
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    @DisplayName("given all children command, when creating budgets, then save all subtree budgets")
    void givenAllChildrenCommand_whenCreatingBudgets_thenSaveAllSubtreeBudgets() {
        // given
        final var ou1 = HierarchyTestFactory.randomOrganizationalUnit();
        final var ou2 = HierarchyTestFactory.randomOrganizationalUnit();
        final var owner = EmployeeTestFactory.randomEmployeeId();
        final var amount = BudgetingTestFactory.randomBigDecimalAmount();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var command = new CreateOrganizationalUnitBudgetsCommand(
                amount, fiscalYear, ou1.id(), Set.of(owner), true, Set.of(), 0, 20);
        when(getSubtreeOrganizationalUnitsUseCase.execute(ou1.id())).thenReturn(List.of(ou1, ou2));
        when(getOrganizationalUnitUseCase.execute(ou1.id())).thenReturn(ou1);
        when(getOrganizationalUnitUseCase.execute(ou2.id())).thenReturn(ou2);
        when(idGenerator.generate()).thenReturn(101L, 102L);

        // when
        final var result = service.execute(command);

        // then
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.items()).hasSize(2);
        verify(assignOwnerUseCase, times(2)).execute(any(AssignOwnerCommand.class));
        verify(budgetRepository, times(2)).save(any(Budget.class));
    }

    @Test
    @DisplayName("given target child ou ids, when creating budgets, then save only filtered subtree budgets")
    void givenTargetChildOuIds_whenCreatingBudgets_thenSaveOnlyFilteredSubtreeBudgets() {
        // given
        final var ou1 = HierarchyTestFactory.randomOrganizationalUnit();
        final var ou2 = HierarchyTestFactory.randomOrganizationalUnit();
        final var ou3 = HierarchyTestFactory.randomOrganizationalUnit();
        final var owner = EmployeeTestFactory.randomEmployeeId();
        final var amount = BudgetingTestFactory.randomBigDecimalAmount();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var command = new CreateOrganizationalUnitBudgetsCommand(
                amount, fiscalYear, ou1.id(), Set.of(owner), false, Set.of(ou2.id()), 0, 20);
        when(getSubtreeOrganizationalUnitsUseCase.execute(ou1.id())).thenReturn(List.of(ou1, ou2, ou3));
        when(getOrganizationalUnitUseCase.execute(ou1.id())).thenReturn(ou1);
        when(getOrganizationalUnitUseCase.execute(ou2.id())).thenReturn(ou2);
        when(idGenerator.generate()).thenReturn(201L, 202L);

        // when
        final var result = service.execute(command);

        // then
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.items()).hasSize(2);
        verify(assignOwnerUseCase, times(2)).execute(any(AssignOwnerCommand.class));
        verify(budgetRepository, times(2)).save(any(Budget.class));
    }

    @Test
    @DisplayName("given existing owners, when assigning duplicate and new owners, then owners are idempotent")
    void givenExistingOwners_whenAssigningDuplicateAndNewOwners_thenOwnersAreIdempotent() {
        // given
        final var existingOwner = EmployeeTestFactory.randomEmployeeId();
        final var newOwner = EmployeeTestFactory.randomEmployeeId();
        final var ou = OrganizationalUnit.create(
                HierarchyTestFactory.randomOrganizationalUnitId(),
                HierarchyTestFactory.randomName(),
                null).addOwner(existingOwner);
        final var updatedOu = ou.addOwner(newOwner);
        final var amount = BudgetingTestFactory.randomBigDecimalAmount();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var command = new CreateOrganizationalUnitBudgetsCommand(
                amount, fiscalYear, ou.id(), Set.of(existingOwner, newOwner), false, Set.of(), 0, 20);
        when(getOrganizationalUnitUseCase.execute(ou.id())).thenReturn(updatedOu);
        when(idGenerator.generate()).thenReturn(301L);

        // when
        final var result = service.execute(command);

        // then
        assertThat(result.items().getFirst().owners())
                .containsExactlyInAnyOrder(existingOwner, newOwner);
    }

    @Test
    @DisplayName("given empty owners, when creating command, then throws exception")
    void givenEmptyOwners_whenCreatingCommand_thenThrowsException() {
        // given
        final var ouId = HierarchyTestFactory.randomOrganizationalUnitId();
        final var amount = BudgetingTestFactory.randomBigDecimalAmount();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();

        // when
        final var executable = (org.assertj.core.api.ThrowableAssert.ThrowingCallable) () ->
                new CreateOrganizationalUnitBudgetsCommand(amount, fiscalYear, ouId, Set.of(), false, Set.of(), 0, 20);

        // then
        assertThatThrownBy(executable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("given null owners, when creating command, then throws exception")
    void givenNullOwners_whenCreatingCommand_thenThrowsException() {
        // given
        final var ouId = HierarchyTestFactory.randomOrganizationalUnitId();
        final var amount = BudgetingTestFactory.randomBigDecimalAmount();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();

        // when
        final var executable = (org.assertj.core.api.ThrowableAssert.ThrowingCallable) () ->
                new CreateOrganizationalUnitBudgetsCommand(amount, fiscalYear, ouId, null, false, Set.of(), 0, 20);

        // then
        assertThatThrownBy(executable).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("given null target children and null page and size, when creating budgets, then defaults apply")
    void givenNullTargetChildrenAndNullPageAndSize_whenCreatingBudgets_thenDefaultsApply() {
        // given
        final var ou = HierarchyTestFactory.randomOrganizationalUnit();
        final var owner = EmployeeTestFactory.randomEmployeeId();
        final var amount = BudgetingTestFactory.randomBigDecimalAmount();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var command = new CreateOrganizationalUnitBudgetsCommand(
                amount, fiscalYear, ou.id(), Set.of(owner), false, null, null, null);
        when(getOrganizationalUnitUseCase.execute(ou.id())).thenReturn(ou);
        when(idGenerator.generate()).thenReturn(401L);

        // when
        final var result = service.execute(command);

        // then
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(20);
        assertThat(result.items()).hasSize(1);
    }

    @Test
    @DisplayName("given negative page and non-positive size, when creating budgets, then fallback defaults apply")
    void givenNegativePageAndNonPositiveSize_whenCreatingBudgets_thenFallbackDefaultsApply() {
        // given
        final var ou = HierarchyTestFactory.randomOrganizationalUnit();
        final var owner = EmployeeTestFactory.randomEmployeeId();
        final var amount = BudgetingTestFactory.randomBigDecimalAmount();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var command = new CreateOrganizationalUnitBudgetsCommand(
                amount, fiscalYear, ou.id(), Set.of(owner), false, Set.of(), -1, 0);
        when(getOrganizationalUnitUseCase.execute(ou.id())).thenReturn(ou);
        when(idGenerator.generate()).thenReturn(501L);

        // when
        final var result = service.execute(command);

        // then
        assertThat(result.page()).isZero();
        assertThat(result.size()).isEqualTo(20);
        assertThat(result.items()).hasSize(1);
    }

    @Test
    @DisplayName("given existing budget for OU, when creating budgets, then throws exception")
    void givenExistingBudgetForOu_whenCreatingBudgets_thenThrowsException() {
        // given
        final var ou = HierarchyTestFactory.randomOrganizationalUnit();
        final var owner = EmployeeTestFactory.randomEmployeeId();
        final var amount = BudgetingTestFactory.randomBigDecimalAmount();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var command = new CreateOrganizationalUnitBudgetsCommand(
                amount, fiscalYear, ou.id(), Set.of(owner), false, Set.of(), 0, 20);
        when(getOrganizationalUnitUseCase.execute(ou.id())).thenReturn(ou);
        when(budgetRepository.existsByOrganizationalUnitId(ou.id())).thenReturn(true);

        // when
        final var executable = (org.assertj.core.api.ThrowableAssert.ThrowingCallable) () -> service.execute(command);

        // then
        assertThatThrownBy(executable)
                .isInstanceOf(com.example.oulearning.budgeting.domain.exception.InvalidBudgetOperationException.class)
                .hasMessageContaining("A budget already exists for organizational unit %d".formatted(ou.id().value()));
    }

    @Test
    @DisplayName("given existing budget for child OU, when creating subtree budgets, then throws exception")
    void givenExistingBudgetForChildOu_whenCreatingSubtreeBudgets_thenThrowsException() {
        // given
        final var ou1 = HierarchyTestFactory.randomOrganizationalUnit();
        final var ou2 = HierarchyTestFactory.randomOrganizationalUnit();
        final var owner = EmployeeTestFactory.randomEmployeeId();
        final var amount = BudgetingTestFactory.randomBigDecimalAmount();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var command = new CreateOrganizationalUnitBudgetsCommand(
                amount, fiscalYear, ou1.id(), Set.of(owner), true, Set.of(), 0, 20);
        when(getSubtreeOrganizationalUnitsUseCase.execute(ou1.id())).thenReturn(List.of(ou1, ou2));
        when(budgetRepository.existsByOrganizationalUnitId(ou1.id())).thenReturn(false);
        when(budgetRepository.existsByOrganizationalUnitId(ou2.id())).thenReturn(true);

        // when
        final var executable = (org.assertj.core.api.ThrowableAssert.ThrowingCallable) () -> service.execute(command);

        // then
        assertThatThrownBy(executable)
                .isInstanceOf(com.example.oulearning.budgeting.domain.exception.InvalidBudgetOperationException.class)
                .hasMessageContaining("A budget already exists for organizational unit %d".formatted(ou2.id().value()));
    }
}
