package com.example.oulearning.budgeting.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.budgeting.application.port.in.command.CreateBudgetCommand;
import com.example.oulearning.budgeting.domain.exception.InvalidBudgetOperationException;
import com.example.oulearning.budgeting.domain.model.Budget;
import com.example.oulearning.budgeting.domain.model.BudgetingTestFactory;
import com.example.oulearning.budgeting.domain.model.IdGenerator;
import com.example.oulearning.budgeting.domain.repository.BudgetRepository;
import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CreateBudgetServiceTest {

    private final BudgetRepository repository = mock(BudgetRepository.class);
    private final IdGenerator idGenerator = mock(IdGenerator.class);
    private final CreateBudgetService service = new CreateBudgetService(repository, idGenerator);

    @Test
    @DisplayName("given valid command, when creating budget, then budget is saved and id is returned")
    void givenValidCommand_whenCreatingBudget_thenBudgetIsSavedAndIdReturned() {
        // given
        final var generatedId = BudgetingTestFactory.randomId();
        final var ouId = HierarchyTestFactory.randomOrganizationalUnitId();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var total = BudgetingTestFactory.randomBigDecimalAmount();
        final var reserved = BudgetingTestFactory.randomBigDecimalAmount();
        final var available = BudgetingTestFactory.randomBigDecimalAmount();
        final var command = new CreateBudgetCommand(ouId, fiscalYear, total, reserved, available);
        when(repository.existsByOrganizationalUnitId(ouId)).thenReturn(false);
        when(idGenerator.generate()).thenReturn(generatedId);

        // when
        final var resultId = service.execute(command);

        // then
        assertThat(resultId.value()).isEqualTo(generatedId);
        final var captor = ArgumentCaptor.forClass(Budget.class);
        verify(repository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.id().value()).isEqualTo(generatedId);
        assertThat(saved.organizationalUnitId()).isEqualTo(ouId);
        assertThat(saved.fiscalYear().value()).isEqualTo(fiscalYear);
        assertThat(saved.total().amount()).isEqualTo(total);
        assertThat(saved.reserved().amount()).isEqualTo(reserved);
        assertThat(saved.available().amount()).isEqualTo(available);
        assertThat(saved.active()).isTrue();
    }

    @Test
    @DisplayName("given existing budget for OU, when creating budget, then throws exception")
    void givenExistingBudgetForOu_whenCreatingBudget_thenThrowsException() {
        // given
        final var ouId = HierarchyTestFactory.randomOrganizationalUnitId();
        final var fiscalYear = BudgetingTestFactory.randomFiscalYearValue();
        final var total = BudgetingTestFactory.randomBigDecimalAmount();
        final var reserved = BudgetingTestFactory.randomBigDecimalAmount();
        final var available = BudgetingTestFactory.randomBigDecimalAmount();
        final var command = new CreateBudgetCommand(ouId, fiscalYear, total, reserved, available);
        when(repository.existsByOrganizationalUnitId(ouId)).thenReturn(true);

        // when
        final var executable = (org.assertj.core.api.ThrowableAssert.ThrowingCallable) () -> service.execute(command);

        // then
        assertThatThrownBy(executable)
                .isInstanceOf(InvalidBudgetOperationException.class)
                .hasMessageContaining("A budget already exists for organizational unit %d".formatted(ouId.value()));
    }
}
