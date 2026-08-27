package com.example.oulearning.budgeting.application.service;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetSubtreeOrganizationalUnitsUseCase;
import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
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
    private final CreateOrganizationalUnitBudgetsService service = new CreateOrganizationalUnitBudgetsService(
            budgetRepository, idGenerator, getOrganizationalUnitUseCase, getSubtreeOrganizationalUnitsUseCase);

    @Test
    @DisplayName("given single OU command, when creating budgets, then save single budget and return result")
    void givenSingleOuCommand_whenCreatingBudgets_thenSaveSingleBudgetAndReturnResult() {
        // given
        final var ou = HierarchyTestFactory.randomOrganizationalUnit();
        final var amount = BudgetingTestFactory.randomBigDecimalAmount();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var command = new CreateOrganizationalUnitBudgetsCommand(amount, fiscalYear, ou.id(), false, Set.of(), 0, 20);
        when(getOrganizationalUnitUseCase.execute(ou.id())).thenReturn(ou);
        when(idGenerator.generate()).thenReturn(100L);

        // when
        final var result = service.execute(command);

        // then
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.items()).hasSize(1);
        verify(budgetRepository, times(1)).save(any(Budget.class));
    }

    @Test
    @DisplayName("given all children command, when creating budgets, then save all subtree budgets")
    void givenAllChildrenCommand_whenCreatingBudgets_thenSaveAllSubtreeBudgets() {
        // given
        final var ou1 = HierarchyTestFactory.randomOrganizationalUnit();
        final var ou2 = HierarchyTestFactory.randomOrganizationalUnit();
        final var amount = BudgetingTestFactory.randomBigDecimalAmount();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var command = new CreateOrganizationalUnitBudgetsCommand(amount, fiscalYear, ou1.id(), true, Set.of(), 0, 20);
        when(getSubtreeOrganizationalUnitsUseCase.execute(ou1.id())).thenReturn(List.of(ou1, ou2));
        when(idGenerator.generate()).thenReturn(101L, 102L);

        // when
        final var result = service.execute(command);

        // then
        assertThat(result.totalElements()).isEqualTo(2);
        assertThat(result.items()).hasSize(2);
        verify(budgetRepository, times(2)).save(any(Budget.class));
    }
}
