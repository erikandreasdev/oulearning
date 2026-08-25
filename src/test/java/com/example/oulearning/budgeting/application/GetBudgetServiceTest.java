package com.example.oulearning.budgeting.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.oulearning.budgeting.domain.BudgetRepository;
import com.example.oulearning.budgeting.domain.BudgetingTestFactory;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetBudgetServiceTest {

    private final BudgetRepository repository = mock(BudgetRepository.class);
    private final GetBudgetService service = new GetBudgetService(repository);

    @Test
    @DisplayName("given existing budget id, when getting budget, then budget is returned")
    void givenExistingBudgetId_whenGettingBudget_thenBudgetIsReturned() {
        // given
        final var budget = BudgetingTestFactory.randomBudget();
        when(repository.findById(budget.id())).thenReturn(Optional.of(budget));

        // when
        final var result = service.execute(budget.id());

        // then
        assertThat(result).isEqualTo(budget);
    }

    @Test
    @DisplayName("given non-existing budget id, when getting budget, then throw BudgetNotFoundException")
    void givenNonExistingBudgetId_whenGettingBudget_thenThrowBudgetNotFoundException() {
        // given
        final var id = BudgetingTestFactory.randomBudgetId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(id))
                .isInstanceOf(BudgetNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
