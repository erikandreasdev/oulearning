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

class UpdateBudgetServiceTest {

    private final BudgetRepository repository = mock(BudgetRepository.class);
    private final UpdateBudgetService service = new UpdateBudgetService(repository);

    @Test
    @DisplayName("given existing budget, when updating amounts, then updated budget is saved")
    void givenExistingBudget_whenUpdatingAmounts_thenUpdatedBudgetIsSaved() {
        // given
        final var budget = BudgetingTestFactory.randomBudget();
        final var newTotal = BudgetingTestFactory.randomBigDecimalAmount();
        final var newReserved = BudgetingTestFactory.randomBigDecimalAmount();
        final var newAvailable = BudgetingTestFactory.randomBigDecimalAmount();
        final var command = new UpdateBudgetCommand(budget.id(), newTotal, newReserved, newAvailable);
        when(repository.findById(budget.id())).thenReturn(Optional.of(budget));

        // when
        service.execute(command);

        // then
        final var captor = ArgumentCaptor.forClass(Budget.class);
        verify(repository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.id()).isEqualTo(budget.id());
        assertThat(saved.total().amount()).isEqualTo(newTotal);
        assertThat(saved.reserved().amount()).isEqualTo(newReserved);
        assertThat(saved.available().amount()).isEqualTo(newAvailable);
    }

    @Test
    @DisplayName("given non-existing budget, when updating, then throw BudgetNotFoundException")
    void givenNonExistingBudget_whenUpdating_thenThrowBudgetNotFoundException() {
        // given
        final var id = BudgetingTestFactory.randomBudgetId();
        final var command = new UpdateBudgetCommand(
                id,
                BudgetingTestFactory.randomBigDecimalAmount(),
                BudgetingTestFactory.randomBigDecimalAmount(),
                BudgetingTestFactory.randomBigDecimalAmount());
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(BudgetNotFoundException.class);
    }
}
