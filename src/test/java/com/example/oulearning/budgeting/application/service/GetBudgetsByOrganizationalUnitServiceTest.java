package com.example.oulearning.budgeting.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.oulearning.budgeting.domain.model.BudgetingTestFactory;
import com.example.oulearning.budgeting.domain.repository.BudgetRepository;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetSubtreeOrganizationalUnitsUseCase;
import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetBudgetsByOrganizationalUnitServiceTest {

    private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
    private final GetOrganizationalUnitUseCase getOrganizationalUnitUseCase = mock(GetOrganizationalUnitUseCase.class);
    private final GetSubtreeOrganizationalUnitsUseCase getSubtreeOrganizationalUnitsUseCase =
            mock(GetSubtreeOrganizationalUnitsUseCase.class);
    private final GetBudgetsByOrganizationalUnitService service = new GetBudgetsByOrganizationalUnitService(
            budgetRepository, getOrganizationalUnitUseCase, getSubtreeOrganizationalUnitsUseCase);

    @Test
    @DisplayName("given single OU query without subtree, when executing, then return single OU budgets")
    void givenSingleOuQueryWithoutSubtree_whenExecuting_thenReturnSingleOuBudgets() {
        // given
        final var ou = HierarchyTestFactory.randomOrganizationalUnit();
        final var budget = BudgetingTestFactory.randomBudget();
        when(getOrganizationalUnitUseCase.execute(ou.id())).thenReturn(ou);
        when(budgetRepository.findByOrganizationalUnitId(ou.id())).thenReturn(List.of(budget));

        // when
        final var results = service.execute(ou.id(), false);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.getFirst().id()).isEqualTo(budget.id());
    }

    @Test
    @DisplayName("given OU query with subtree, when executing, then return all subtree budgets")
    void givenOuQueryWithSubtree_whenExecuting_thenReturnAllSubtreeBudgets() {
        // given
        final var ou1 = HierarchyTestFactory.randomOrganizationalUnit();
        final var ou2 = HierarchyTestFactory.randomOrganizationalUnit();
        final var budget1 = BudgetingTestFactory.randomBudget();
        final var budget2 = BudgetingTestFactory.randomBudget();
        when(getSubtreeOrganizationalUnitsUseCase.execute(ou1.id())).thenReturn(List.of(ou1, ou2));
        when(budgetRepository.findByOrganizationalUnitId(ou1.id())).thenReturn(List.of(budget1));
        when(budgetRepository.findByOrganizationalUnitId(ou2.id())).thenReturn(List.of(budget2));

        // when
        final var results = service.execute(ou1.id(), true);

        // then
        assertThat(results).hasSize(2);
    }
}
