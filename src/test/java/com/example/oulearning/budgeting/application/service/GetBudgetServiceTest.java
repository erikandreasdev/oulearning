package com.example.oulearning.budgeting.application.service;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.port.in.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.port.in.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.port.in.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.port.in.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.oulearning.budgeting.domain.repository.BudgetRepository;
import com.example.oulearning.budgeting.domain.model.BudgetingTestFactory;
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
