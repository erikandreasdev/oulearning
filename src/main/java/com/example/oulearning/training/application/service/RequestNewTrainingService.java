package com.example.oulearning.training.application.service;

import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetOrganizationalUnitUseCase;
import com.example.oulearning.training.application.port.in.command.RequestNewTrainingCommand;
import com.example.oulearning.training.application.port.in.usecase.RequestNewTrainingUseCase;
import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import com.example.oulearning.training.domain.model.Cost;
import com.example.oulearning.training.domain.model.Hours;
import com.example.oulearning.training.domain.model.IdGenerator;
import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.model.TrainingName;
import com.example.oulearning.training.domain.model.TrainingPurpose;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import java.time.Clock;
import org.springframework.stereotype.Service;

@Service
public class RequestNewTrainingService implements RequestNewTrainingUseCase {

    private final TrainingRepository trainingRepository;
    private final IdGenerator idGenerator;
    private final GetOrganizationalUnitUseCase getOrganizationalUnitUseCase;
    private final Clock clock;

    public RequestNewTrainingService(
            final TrainingRepository trainingRepository,
            final IdGenerator idGenerator,
            final GetOrganizationalUnitUseCase getOrganizationalUnitUseCase,
            final Clock clock) {
        this.trainingRepository = trainingRepository;
        this.idGenerator = idGenerator;
        this.getOrganizationalUnitUseCase = getOrganizationalUnitUseCase;
        this.clock = clock;
    }

    @Override
    public Training execute(final RequestNewTrainingCommand command) {
        final var ou = getOrganizationalUnitUseCase.execute(command.organizationalUnitId());

        if (!ou.owners().contains(command.requestedBy())) {
            throw new InvalidTrainingOperationException(
                    "Requester %d must be an owner of organizational unit %d"
                            .formatted(command.requestedBy().value(), command.organizationalUnitId().value()));
        }

        if (command.attendees() != null) {
            for (final var attendee : command.attendees()) {
                if (!ou.members().contains(attendee)) {
                    throw new InvalidTrainingOperationException(
                            "Attendee %d is not a member of organizational unit %d"
                                    .formatted(attendee.value(), command.organizationalUnitId().value()));
                }
            }
        }

        final var purpose = command.purposeType() == TrainingPurposeType.OTHER
                ? TrainingPurpose.other(command.purposeDescription())
                : command.purposeType() == TrainingPurposeType.INDIVIDUAL_DEVELOPMENT_PLAN
                        ? TrainingPurpose.individualDevelopmentPlan()
                        : TrainingPurpose.departmentGoals();

        final var now = clock.instant();
        final var trainingId = new TrainingId(idGenerator.generate());
        var training = Training.create(
                trainingId,
                command.requestedBy(),
                command.organizationalUnitId(),
                new TrainingName(command.name()),
                Cost.of(command.costAmount(), command.currency()),
                new Hours(command.hours()),
                purpose,
                command.typeId(),
                now);

        if (command.attendees() != null) {
            for (final var attendee : command.attendees()) {
                training = training.addAttendee(attendee, now);
            }
        }

        trainingRepository.save(training);
        return training;
    }
}
