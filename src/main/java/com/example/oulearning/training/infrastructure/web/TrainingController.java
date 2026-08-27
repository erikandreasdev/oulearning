package com.example.oulearning.training.infrastructure.web;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.application.port.in.command.ListTrainingRequestsQuery;
import com.example.oulearning.training.application.port.in.command.RequestNewTrainingCommand;
import com.example.oulearning.training.application.port.in.command.UpdateTrainingReviewCommand;
import com.example.oulearning.training.application.port.in.usecase.GetAreaTrainingsUseCase;
import com.example.oulearning.training.application.port.in.usecase.GetTrainingDetailsUseCase;
import com.example.oulearning.training.application.port.in.usecase.GetTrainingUseCase;
import com.example.oulearning.training.application.port.in.usecase.ListTrainingRequestsUseCase;
import com.example.oulearning.training.application.port.in.usecase.RequestNewTrainingUseCase;
import com.example.oulearning.training.application.port.in.usecase.UpdateTrainingReviewUseCase;
import com.example.oulearning.training.domain.model.ExternalProviderId;
import com.example.oulearning.training.domain.model.Modality;
import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.domain.model.TrainingStatus;
import com.example.oulearning.training.domain.model.TypeId;
import com.example.oulearning.training.infrastructure.web.api.TrainingsApi;
import com.example.oulearning.training.infrastructure.web.dto.AreaTrainingItem;
import com.example.oulearning.training.infrastructure.web.dto.AreaTrainingsResponse;
import com.example.oulearning.training.infrastructure.web.dto.PaginatedTrainingRequestsResponse;
import com.example.oulearning.training.infrastructure.web.dto.RequestNewTrainingRequest;
import com.example.oulearning.training.infrastructure.web.dto.TrainingAttendeeDetails;
import com.example.oulearning.training.infrastructure.web.dto.TrainingDetailsResponse;
import com.example.oulearning.training.infrastructure.web.dto.TrainingResponse;
import com.example.oulearning.training.infrastructure.web.dto.UpdateTrainingReviewRequest;
import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
class TrainingController implements TrainingsApi {

    private final GetAreaTrainingsUseCase getAreaTrainingsUseCase;
    private final GetTrainingDetailsUseCase getTrainingDetailsUseCase;
    private final RequestNewTrainingUseCase requestNewTrainingUseCase;
    private final ListTrainingRequestsUseCase listTrainingRequestsUseCase;
    private final UpdateTrainingReviewUseCase updateTrainingReviewUseCase;
    private final GetTrainingUseCase getTrainingUseCase;

    TrainingController(
            final GetAreaTrainingsUseCase getAreaTrainingsUseCase,
            final GetTrainingDetailsUseCase getTrainingDetailsUseCase,
            final RequestNewTrainingUseCase requestNewTrainingUseCase,
            final ListTrainingRequestsUseCase listTrainingRequestsUseCase,
            final UpdateTrainingReviewUseCase updateTrainingReviewUseCase,
            final GetTrainingUseCase getTrainingUseCase) {
        this.getAreaTrainingsUseCase = getAreaTrainingsUseCase;
        this.getTrainingDetailsUseCase = getTrainingDetailsUseCase;
        this.requestNewTrainingUseCase = requestNewTrainingUseCase;
        this.listTrainingRequestsUseCase = listTrainingRequestsUseCase;
        this.updateTrainingReviewUseCase = updateTrainingReviewUseCase;
        this.getTrainingUseCase = getTrainingUseCase;
    }

    @Override
    public ResponseEntity<AreaTrainingsResponse> getAreaTrainings(final Long organizationalUnitId) {
        final var overview = getAreaTrainingsUseCase.execute(new OrganizationalUnitId(organizationalUnitId));
        final var response = new AreaTrainingsResponse();
        response.setAssignedBudget(overview.assignedBudget());
        response.setAvailableBudget(overview.availableBudget());
        final var items = overview.trainings().stream().map(t -> {
            final var item = new AreaTrainingItem();
            item.setId(t.id().value());
            item.setName(t.name().value());
            item.setOrganizationalUnitIds(t.organizationalUnitIds().stream().map(OrganizationalUnitId::value).toList());
            item.setCostAmount(t.cost().amount());
            item.setCostCurrency(t.cost().currency());
            item.setStatus(t.status().name());
            return item;
        }).toList();
        response.setTrainings(items);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TrainingDetailsResponse> getTrainingDetails(final Long id) {
        final var details = getTrainingDetailsUseCase.execute(new TrainingId(id));
        final var response = new TrainingDetailsResponse();
        response.setId(details.id().value());
        response.setName(details.name().value());
        response.setCostAmount(details.cost().amount());
        response.setCostCurrency(details.cost().currency());
        response.setRequestedByName(details.requestedByName());
        response.setPurposeType(details.purpose().type().name());
        response.setPurposeDescription(details.purpose().optionalOtherPurpose().orElse(null));
        response.setTypeId(details.typeId().value());
        response.setHours(details.hours().value());
        final var attendees = details.attendees().stream().map(a -> {
            final var attendee = new TrainingAttendeeDetails();
            attendee.setId(a.id().value());
            attendee.setName(a.name());
            attendee.setEmail(a.email());
            return attendee;
        }).toList();
        response.setAttendees(attendees);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TrainingResponse> requestNewTraining(final RequestNewTrainingRequest request) {
        final var attendees = request.getAttendees() != null
                ? request.getAttendees().stream().map(EmployeeId::new).collect(Collectors.toSet())
                : Set.<EmployeeId>of();

        final var command = new RequestNewTrainingCommand(
                new EmployeeId(request.getRequestedBy()),
                new OrganizationalUnitId(request.getOrganizationalUnitId()),
                request.getName(),
                request.getCostAmount(),
                request.getCostCurrency(),
                request.getHours(),
                TrainingPurposeType.valueOf(request.getPurposeType()),
                request.getPurposeDescription(),
                new TypeId(request.getTypeId()),
                attendees);

        final var training = requestNewTrainingUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(training));
    }

    @Override
    public ResponseEntity<PaginatedTrainingRequestsResponse> listTrainingRequests(
            final String name,
            final BigDecimal costAmount,
            final Long organizationalUnitId,
            final String purposeType,
            final Long typeId,
            final Integer hours,
            final String status,
            final Integer page,
            final Integer size) {
        final var query = new ListTrainingRequestsQuery(
                name,
                costAmount,
                organizationalUnitId != null ? new OrganizationalUnitId(organizationalUnitId) : null,
                purposeType != null ? TrainingPurposeType.valueOf(purposeType) : null,
                typeId != null ? new TypeId(typeId) : null,
                hours,
                status != null ? TrainingStatus.valueOf(status) : null,
                page,
                size);

        final var result = listTrainingRequestsUseCase.execute(query);
        final var response = new PaginatedTrainingRequestsResponse();
        response.setItems(result.items().stream().map(this::toResponse).toList());
        response.setTotalElements(result.totalElements());
        response.setTotalPages(result.totalPages());
        response.setPage(result.page());
        response.setSize(result.size());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<TrainingResponse> updateTrainingReview(
            final Long id, final UpdateTrainingReviewRequest request) {
        final var command = new UpdateTrainingReviewCommand(
                new TrainingId(id),
                request.getComments(),
                Modality.valueOf(request.getModality()),
                request.getStartDate().toInstant(),
                request.getEndDate().toInstant(),
                request.getExternalProviderId() != null ? new ExternalProviderId(request.getExternalProviderId()) : null,
                request.getReviewedAt().toInstant());

        final var updated = updateTrainingReviewUseCase.execute(command);
        return ResponseEntity.ok(toResponse(updated));
    }

    @Override
    public ResponseEntity<TrainingResponse> getTraining(final Long id) {
        final var training = getTrainingUseCase.execute(new TrainingId(id));
        return ResponseEntity.ok(toResponse(training));
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
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
