package com.example.oulearning.training.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetOrganizationalUnitUseCase;
import com.example.oulearning.organization.domain.employee.model.EmployeeTestFactory;
import com.example.oulearning.organization.domain.hierarchy.model.HierarchyTestFactory;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.training.application.port.in.command.RequestNewTrainingCommand;
import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import com.example.oulearning.training.domain.model.IdGenerator;
import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.domain.model.TrainingTestFactory;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class RequestNewTrainingServiceTest {

    private final TrainingRepository trainingRepository = mock(TrainingRepository.class);
    private final IdGenerator idGenerator = mock(IdGenerator.class);
    private final GetOrganizationalUnitUseCase getOrganizationalUnitUseCase = mock(GetOrganizationalUnitUseCase.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-08-27T10:00:00Z"), ZoneOffset.UTC);
    private final RequestNewTrainingService service = new RequestNewTrainingService(
            trainingRepository, idGenerator, getOrganizationalUnitUseCase, clock);

    @Test
    @DisplayName("given owner requester and member attendees, when requesting training, then training is created and saved")
    void givenOwnerRequesterAndMemberAttendees_whenRequestingTraining_thenTrainingIsCreatedAndSaved() {
        // given
        final var requester = EmployeeTestFactory.randomEmployeeId();
        final var attendee = EmployeeTestFactory.randomEmployeeId();
        final var ouId = HierarchyTestFactory.randomOrganizationalUnitId();
        final var ou = OrganizationalUnit.reconstitute(
                ouId,
                HierarchyTestFactory.randomName(),
                null,
                Set.of(),
                Set.of(requester),
                Set.of(attendee),
                true);
        when(getOrganizationalUnitUseCase.execute(ouId)).thenReturn(ou);
        when(idGenerator.generate()).thenReturn(100L);

        final var command = new RequestNewTrainingCommand(
                requester,
                ouId,
                "Java Deep Dive",
                BigDecimal.valueOf(500),
                "EUR",
                20,
                TrainingPurposeType.DEPARTMENT_GOALS,
                null,
                TrainingTestFactory.randomTypeId(),
                Set.of(attendee));

        // when
        final var result = service.execute(command);

        // then
        assertThat(result.id().value()).isEqualTo(100L);
        assertThat(result.attendees()).containsExactly(attendee);
        final var captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingRepository).save(captor.capture());
    }

    @Test
    @DisplayName("given requester not owner, when requesting training, then throw InvalidTrainingOperationException")
    void givenRequesterNotOwner_whenRequestingTraining_thenThrowInvalidTrainingOperationException() {
        // given
        final var requester = EmployeeTestFactory.randomEmployeeId();
        final var otherOwner = EmployeeTestFactory.randomEmployeeId();
        final var ouId = HierarchyTestFactory.randomOrganizationalUnitId();
        final var ou = OrganizationalUnit.reconstitute(
                ouId,
                HierarchyTestFactory.randomName(),
                null,
                Set.of(),
                Set.of(otherOwner),
                Set.of(),
                true);
        when(getOrganizationalUnitUseCase.execute(ouId)).thenReturn(ou);

        final var command = new RequestNewTrainingCommand(
                requester,
                ouId,
                "Java Deep Dive",
                BigDecimal.valueOf(500),
                "EUR",
                20,
                TrainingPurposeType.DEPARTMENT_GOALS,
                null,
                TrainingTestFactory.randomTypeId(),
                Set.of());

        // when

        // then
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(InvalidTrainingOperationException.class)
                .hasMessageContaining("must be an owner");
    }

    @Test
    @DisplayName("given attendee not member, when requesting training, then throw InvalidTrainingOperationException")
    void givenAttendeeNotMember_whenRequestingTraining_thenThrowInvalidTrainingOperationException() {
        // given
        final var requester = EmployeeTestFactory.randomEmployeeId();
        final var nonMemberAttendee = EmployeeTestFactory.randomEmployeeId();
        final var ouId = HierarchyTestFactory.randomOrganizationalUnitId();
        final var ou = OrganizationalUnit.reconstitute(
                ouId,
                HierarchyTestFactory.randomName(),
                null,
                Set.of(),
                Set.of(requester),
                Set.of(),
                true);
        when(getOrganizationalUnitUseCase.execute(ouId)).thenReturn(ou);

        final var command = new RequestNewTrainingCommand(
                requester,
                ouId,
                "Java Deep Dive",
                BigDecimal.valueOf(500),
                "EUR",
                20,
                TrainingPurposeType.DEPARTMENT_GOALS,
                null,
                TrainingTestFactory.randomTypeId(),
                Set.of(nonMemberAttendee));

        // when

        // then
        assertThatThrownBy(() -> service.execute(command))
                .isInstanceOf(InvalidTrainingOperationException.class)
                .hasMessageContaining("is not a member");
    }
}
