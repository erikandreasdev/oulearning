package com.example.oulearning.budgeting.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.budgeting.domain.Budget;
import com.example.oulearning.budgeting.domain.BudgetRepository;
import com.example.oulearning.budgeting.domain.BudgetingTestFactory;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class DeleteBudgetServiceTest {

    private final BudgetRepository repository = mock(BudgetRepository.class);
    private final DeleteBudgetService service = new DeleteBudgetService(repository);

    @Test
    @DisplayName("given existing budget, when deleting, then budget is deactivated and saved")
    void givenExistingBudget_whenDeleting_thenBudgetIsDeactivatedAndSaved() {
        // given
        final var budget = BudgetingTestFactory.randomBudget();
        when(repository.findById(budget.id())).thenReturn(Optional.of(budget));

        // when
        service.execute(budget.id());

        // then
        final var captor = ArgumentCaptor.forClass(Budget.class);
        verify(repository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.id()).isEqualTo(budget.id());
        assertThat(saved.active()).isFalse();
    }

    @Test
    @DisplayName("given non-existing budget, when deleting, then throw BudgetNotFoundException")
    void givenNonExistingBudget_whenDeleting_thenThrowBudgetNotFoundException() {
        // given
        final var id = BudgetingTestFactory.randomBudgetId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(id))
                .isInstanceOf(BudgetNotFoundException.class);
    }
}
