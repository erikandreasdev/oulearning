package com.example.oulearning.training.application.service;
import com.example.oulearning.training.application.port.in.command.UpdateTrainingCommand;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.budgeting.domain.model.BudgetingConstants;
import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import com.example.oulearning.training.domain.model.TrainingTestFactory;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UpdateTrainingServiceTest {

    private final TrainingRepository repository = mock(TrainingRepository.class);
    private final Instant fixedInstant = Instant.parse("2026-08-25T11:00:00Z");
    private final Clock clock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
    private final UpdateTrainingService service = new UpdateTrainingService(repository, clock);

    @Test
    @DisplayName("given existing training, when updating details, then updated training is saved")
    void givenExistingTraining_whenUpdatingDetails_thenUpdatedTrainingIsSaved() {
        // given
        final var training = TrainingTestFactory.randomTraining();
        final var newName = TrainingTestFactory.randomTrainingNameString();
        final var newCost = TrainingTestFactory.randomBigDecimalCostAmount();
        final var newHours = TrainingTestFactory.randomHoursValue();
        final var newTypeId = TrainingTestFactory.randomId();
        final var command = new UpdateTrainingCommand(
                training.id(),
                newName,
                newCost,
                BudgetingConstants.DEFAULT_CURRENCY,
                newHours,
                TrainingPurposeType.DEPARTMENT_GOALS,
                null,
                newTypeId);
        when(repository.findById(training.id())).thenReturn(Optional.of(training));

        // when
        service.execute(command);

        // then
        final var captor = ArgumentCaptor.forClass(Training.class);
        verify(repository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.id()).isEqualTo(training.id());
        assertThat(saved.name().value()).isEqualTo(newName);
        assertThat(saved.cost().amount()).isEqualTo(newCost);
        assertThat(saved.hours().value()).isEqualTo(newHours);
        assertThat(saved.purpose().type()).isEqualTo(TrainingPurposeType.DEPARTMENT_GOALS);
        assertThat(saved.typeId().value()).isEqualTo(newTypeId);
        assertThat(saved.updatedAt()).isEqualTo(fixedInstant);
    }

    @Test
    @DisplayName("given non-existing training, when updating, then throw TrainingNotFoundException")
    void givenNonExistingTraining_whenUpdating_thenThrowTrainingNotFoundException() {
        // given
        final var id = TrainingTestFactory.randomTrainingId();
        final var command = new UpdateTrainingCommand(
                id,
                TrainingTestFactory.randomTrainingNameString(),
                TrainingTestFactory.randomBigDecimalCostAmount(),
                BudgetingConstants.DEFAULT_CURRENCY,
                TrainingTestFactory.randomHoursValue(),
                TrainingPurposeType.INDIVIDUAL_DEVELOPMENT_PLAN,
                null,
                TrainingTestFactory.randomId());
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(TrainingNotFoundException.class);
    }
}
