package com.example.oulearning.training.application.service;

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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.budgeting.domain.model.BudgetingConstants;
import com.example.oulearning.organization.domain.employee.model.EmployeeTestFactory;
import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import com.example.oulearning.training.domain.model.IdGenerator;
import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import com.example.oulearning.training.domain.model.TrainingStatus;
import com.example.oulearning.training.domain.model.TrainingTestFactory;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class CreateTrainingServiceTest {

    private final TrainingRepository repository = mock(TrainingRepository.class);
    private final IdGenerator idGenerator = mock(IdGenerator.class);
    private final Instant fixedInstant = Instant.parse("2026-08-25T10:00:00Z");
    private final Clock clock = Clock.fixed(fixedInstant, ZoneId.of("UTC"));
    private final CreateTrainingService service = new CreateTrainingService(repository, idGenerator, clock);

    @Test
    @DisplayName("given valid command with IDP purpose, when creating training, then training is saved and id returned")
    void givenValidCommandWithIdpPurpose_whenCreatingTraining_thenTrainingIsSavedAndIdReturned() {
        // given
        final var generatedId = TrainingTestFactory.randomId();
        final var requestedBy = EmployeeTestFactory.randomEmployeeId();
        final var ouId = HierarchyTestFactory.randomOrganizationalUnitId();
        final var name = TrainingTestFactory.randomTrainingNameString();
        final var costAmount = TrainingTestFactory.randomBigDecimalCostAmount();
        final var hours = TrainingTestFactory.randomHoursValue();
        final var typeId = TrainingTestFactory.randomId();
        final var command = new CreateTrainingCommand(
                requestedBy,
                ouId,
                name,
                costAmount,
                BudgetingConstants.DEFAULT_CURRENCY,
                hours,
                TrainingPurposeType.INDIVIDUAL_DEVELOPMENT_PLAN,
                null,
                typeId);
        when(idGenerator.generate()).thenReturn(generatedId);

        // when
        final var resultId = service.execute(command);

        // then
        assertThat(resultId.value()).isEqualTo(generatedId);
        final var captor = ArgumentCaptor.forClass(Training.class);
        verify(repository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.id().value()).isEqualTo(generatedId);
        assertThat(saved.requestedBy()).isEqualTo(requestedBy);
        assertThat(saved.organizationalUnitId()).isEqualTo(ouId);
        assertThat(saved.name().value()).isEqualTo(name);
        assertThat(saved.cost().amount()).isEqualTo(costAmount);
        assertThat(saved.hours().value()).isEqualTo(hours);
        assertThat(saved.purpose().type()).isEqualTo(TrainingPurposeType.INDIVIDUAL_DEVELOPMENT_PLAN);
        assertThat(saved.typeId().value()).isEqualTo(typeId);
        assertThat(saved.status()).isEqualTo(TrainingStatus.REQUESTED);
        assertThat(saved.createdAt()).isEqualTo(fixedInstant);
        assertThat(saved.active()).isTrue();
    }

    @Test
    @DisplayName("given valid command with OTHER purpose, when creating training, then other purpose is set")
    void givenValidCommandWithOtherPurpose_whenCreatingTraining_thenOtherPurposeIsSet() {
        // given
        final var generatedId = TrainingTestFactory.randomId();
        final var requestedBy = EmployeeTestFactory.randomEmployeeId();
        final var ouId = HierarchyTestFactory.randomOrganizationalUnitId();
        final var name = TrainingTestFactory.randomTrainingNameString();
        final var costAmount = TrainingTestFactory.randomBigDecimalCostAmount();
        final var hours = TrainingTestFactory.randomHoursValue();
        final var typeId = TrainingTestFactory.randomId();
        final var purposeDesc = TrainingTestFactory.randomPurposeDescription();
        final var command = new CreateTrainingCommand(
                requestedBy,
                ouId,
                name,
                costAmount,
                BudgetingConstants.DEFAULT_CURRENCY,
                hours,
                TrainingPurposeType.OTHER,
                purposeDesc,
                typeId);
        when(idGenerator.generate()).thenReturn(generatedId);

        // when
        final var resultId = service.execute(command);

        // then
        assertThat(resultId.value()).isEqualTo(generatedId);
        final var captor = ArgumentCaptor.forClass(Training.class);
        verify(repository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.purpose().type()).isEqualTo(TrainingPurposeType.OTHER);
        assertThat(saved.purpose().otherPurpose()).isEqualTo(purposeDesc);
    }
}
