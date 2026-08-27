package com.example.oulearning.training.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.oulearning.budgeting.domain.model.BudgetingTestFactory;
import com.example.oulearning.budgeting.domain.repository.BudgetRepository;
import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import com.example.oulearning.training.domain.model.TrainingTestFactory;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetAreaTrainingsServiceTest {

    private final TrainingRepository trainingRepository = mock(TrainingRepository.class);
    private final BudgetRepository budgetRepository = mock(BudgetRepository.class);
    private final GetAreaTrainingsService service = new GetAreaTrainingsService(trainingRepository, budgetRepository);

    @Test
    @DisplayName("given area with budgets and trainings, when executing, then return area overview")
    void givenAreaWithBudgetsAndTrainings_whenExecuting_thenReturnAreaOverview() {
        // given
        final var ouId = HierarchyTestFactory.randomOrganizationalUnitId();
        final var budget = BudgetingTestFactory.randomBudget();
        final var training = TrainingTestFactory.randomTraining();
        when(budgetRepository.findByOrganizationalUnitId(ouId)).thenReturn(List.of(budget));
        when(trainingRepository.findByOrganizationalUnitId(ouId)).thenReturn(List.of(training));

        // when
        final var result = service.execute(ouId);

        // then
        assertThat(result.assignedBudget()).isEqualTo(budget.total().amount());
        assertThat(result.availableBudget()).isEqualTo(budget.available().amount());
        assertThat(result.trainings()).hasSize(1);
    }
}
