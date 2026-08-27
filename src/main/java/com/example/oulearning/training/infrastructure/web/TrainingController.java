package com.example.oulearning.training.infrastructure.web;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.application.port.in.command.CreateTrainingCommand;
import com.example.oulearning.training.application.port.in.command.UpdateTrainingCommand;
import com.example.oulearning.training.application.port.in.usecase.CreateTrainingUseCase;
import com.example.oulearning.training.application.port.in.usecase.DeleteTrainingUseCase;
import com.example.oulearning.training.application.port.in.usecase.GetTrainingUseCase;
import com.example.oulearning.training.application.port.in.usecase.UpdateTrainingUseCase;
import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.infrastructure.web.api.TrainingsApi;
import com.example.oulearning.training.infrastructure.web.dto.CreateTrainingRequest;
import com.example.oulearning.training.infrastructure.web.dto.TrainingResponse;
import com.example.oulearning.training.infrastructure.web.dto.UpdateTrainingRequest;
import java.time.ZoneOffset;
import java.util.ArrayList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TrainingController implements TrainingsApi {

    private final CreateTrainingUseCase createTrainingUseCase;
    private final GetTrainingUseCase getTrainingUseCase;
    private final UpdateTrainingUseCase updateTrainingUseCase;
    private final DeleteTrainingUseCase deleteTrainingUseCase;

    TrainingController(
            final CreateTrainingUseCase createTrainingUseCase,
            final GetTrainingUseCase getTrainingUseCase,
            final UpdateTrainingUseCase updateTrainingUseCase,
            final DeleteTrainingUseCase deleteTrainingUseCase) {
        this.createTrainingUseCase = createTrainingUseCase;
        this.getTrainingUseCase = getTrainingUseCase;
        this.updateTrainingUseCase = updateTrainingUseCase;
        this.deleteTrainingUseCase = deleteTrainingUseCase;
    }

    @Override
    public ResponseEntity<TrainingResponse> createTraining(final CreateTrainingRequest request) {
        final var command = new CreateTrainingCommand(
                new EmployeeId(request.getRequestedBy()),
                new OrganizationalUnitId(request.getOrganizationalUnitId()),
                request.getName(),
                request.getCostAmount(),
                request.getCurrency(),
                request.getHours(),
                TrainingPurposeType.valueOf(request.getPurposeType()),
                request.getPurposeDescription(),
                request.getTypeId());
        final var id = createTrainingUseCase.execute(command);
        final var training = getTrainingUseCase.execute(id);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(training));
    }

    @Override
    public ResponseEntity<TrainingResponse> getTraining(final Long id) {
        final var training = getTrainingUseCase.execute(new TrainingId(id));
        return ResponseEntity.ok(toResponse(training));
    }

    @Override
    public ResponseEntity<TrainingResponse> updateTraining(final Long id, final UpdateTrainingRequest request) {
        final var command = new UpdateTrainingCommand(
                new TrainingId(id),
                request.getName(),
                request.getCostAmount(),
                request.getCurrency(),
                request.getHours(),
                TrainingPurposeType.valueOf(request.getPurposeType()),
                request.getPurposeDescription(),
                request.getTypeId());
        updateTrainingUseCase.execute(command);
        final var training = getTrainingUseCase.execute(new TrainingId(id));
        return ResponseEntity.ok(toResponse(training));
    }

    @Override
    public ResponseEntity<Void> deleteTraining(final Long id) {
        deleteTrainingUseCase.execute(new TrainingId(id));
        return ResponseEntity.noContent().build();
    }

    private TrainingResponse toResponse(final Training training) {
        final var response = new TrainingResponse();
        response.setId(training.id().value());
        response.setRequestedBy(training.requestedBy().value());
        response.setOrganizationalUnitId(training.organizationalUnitId().value());
        response.setName(training.name().value());
        response.setCostAmount(training.cost().amount());
        response.setCurrency(training.cost().currency());
        response.setHours(training.hours().value());
        response.setPurposeType(training.purpose().type().name());
        response.setPurposeDescription(training.purpose().optionalOtherPurpose().orElse(null));
        response.setTypeId(training.typeId().value());
        response.setStatus(training.status().name());

        training.managerReview().ifPresent(review -> {
            response.setManagerReviewModality(review.modality().name());
            response.setManagerReviewComments(review.comments());
            response.setManagerReviewStartDate(java.time.OffsetDateTime.ofInstant(review.startDate(), ZoneOffset.UTC));
            response.setManagerReviewEndDate(java.time.OffsetDateTime.ofInstant(review.endDate(), ZoneOffset.UTC));
            response.setManagerReviewReviewedAt(java.time.OffsetDateTime.ofInstant(review.reviewedAt(), ZoneOffset.UTC));
            review.optionalExternalProviderId().ifPresent(pid -> response.setManagerReviewExternalProviderId(pid.value()));
        });

        response.setCreatedAt(java.time.OffsetDateTime.ofInstant(training.createdAt(), ZoneOffset.UTC));
        response.setUpdatedAt(java.time.OffsetDateTime.ofInstant(training.updatedAt(), ZoneOffset.UTC));

        final var attendees = new ArrayList<Long>();
        training.attendees().forEach(a -> attendees.add(a.value()));
        response.setAttendees(attendees);

        response.setActive(training.active());
        return response;
    }
}
