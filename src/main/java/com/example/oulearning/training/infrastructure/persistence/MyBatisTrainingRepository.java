package com.example.oulearning.training.infrastructure.persistence;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.training.domain.model.Cost;
import com.example.oulearning.training.domain.model.ExternalProviderId;
import com.example.oulearning.training.domain.model.Hours;
import com.example.oulearning.training.domain.model.ManagerReview;
import com.example.oulearning.training.domain.model.Modality;
import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.model.TrainingName;
import com.example.oulearning.training.domain.model.TrainingPurpose;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.domain.model.TrainingStatus;
import com.example.oulearning.training.domain.model.TypeId;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import com.example.oulearning.training.domain.repository.TrainingFilterCriteria;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
class MyBatisTrainingRepository implements TrainingRepository {

    private final TrainingMapper trainingMapper;

    MyBatisTrainingRepository(final TrainingMapper trainingMapper) {
        this.trainingMapper = trainingMapper;
    }

    @Override
    public Optional<Training> findById(final TrainingId id) {
        final var entity = trainingMapper.findById(id.value());
        if (entity.isEmpty()) {
            return Optional.empty();
        }
        final var attendees = trainingMapper.findAttendeesByTrainingId(id.value());
        entity.get().setAttendeeIds(attendees);
        return entity.map(this::toDomain);
    }

    @Override
    public List<Training> findByOrganizationalUnitId(final OrganizationalUnitId organizationalUnitId) {
        final var entities = trainingMapper.findByOrganizationalUnitId(organizationalUnitId.value());
        for (final var entity : entities) {
            entity.setAttendeeIds(trainingMapper.findAttendeesByTrainingId(entity.getId()));
        }
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public List<Training> findAll(final TrainingFilterCriteria criteria, final int offset, final int limit) {
        final String namePattern = criteria != null && criteria.name() != null ? "%s%s%s".formatted("%", criteria.name().value(), "%") : null;
        final var costAmount = criteria != null && criteria.cost() != null ? criteria.cost().amount() : null;
        final var ouId = criteria != null && criteria.organizationalUnitId() != null ? criteria.organizationalUnitId().value() : null;
        final var purposeType = criteria != null && criteria.purposeType() != null ? criteria.purposeType().name() : null;
        final var typeId = criteria != null && criteria.typeId() != null ? criteria.typeId().value() : null;
        final var hours = criteria != null && criteria.hours() != null ? criteria.hours().value() : null;
        final var status = criteria != null && criteria.status() != null ? criteria.status().name() : null;

        final var entities = trainingMapper.findAll(namePattern, costAmount, ouId, purposeType, typeId, hours, status, offset, limit);
        for (final var entity : entities) {
            entity.setAttendeeIds(trainingMapper.findAttendeesByTrainingId(entity.getId()));
        }
        return entities.stream().map(this::toDomain).toList();
    }

    @Override
    public long count(final TrainingFilterCriteria criteria) {
        final String namePattern = criteria != null && criteria.name() != null ? "%s%s%s".formatted("%", criteria.name().value(), "%") : null;
        final var costAmount = criteria != null && criteria.cost() != null ? criteria.cost().amount() : null;
        final var ouId = criteria != null && criteria.organizationalUnitId() != null ? criteria.organizationalUnitId().value() : null;
        final var purposeType = criteria != null && criteria.purposeType() != null ? criteria.purposeType().name() : null;
        final var typeId = criteria != null && criteria.typeId() != null ? criteria.typeId().value() : null;
        final var hours = criteria != null && criteria.hours() != null ? criteria.hours().value() : null;
        final var status = criteria != null && criteria.status() != null ? criteria.status().name() : null;

        return trainingMapper.count(namePattern, costAmount, ouId, purposeType, typeId, hours, status);
    }

    @Override
    public void save(final Training training) {
        final var entity = toEntity(training);
        if (trainingMapper.findById(training.id().value()).isEmpty()) {
            trainingMapper.insert(entity);
            saveAttendees(entity.getId(), training.attendees());
        } else {
            trainingMapper.update(entity);
            trainingMapper.deleteAttendeesByTrainingId(entity.getId());
            saveAttendees(entity.getId(), training.attendees());
        }
    }

    private void saveAttendees(final Long trainingId, final Set<EmployeeId> attendees) {
        if (attendees != null) {
            for (final var attendee : attendees) {
                trainingMapper.insertAttendee(trainingId, attendee.value());
            }
        }
    }

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private Training toDomain(final TrainingEntity entity) {
        ManagerReview managerReview = null;
        if (entity.getManagerReviewModality() != null) {
            managerReview = new ManagerReview(
                    entity.getManagerReviewComments(),
                    Modality.valueOf(entity.getManagerReviewModality()),
                    entity.getManagerReviewStartDate(),
                    entity.getManagerReviewEndDate(),
                    entity.getManagerReviewExternalProviderId() != null ? new ExternalProviderId(entity.getManagerReviewExternalProviderId()) : null,
                    entity.getManagerReviewReviewedAt());
        }

        final var purposeType = TrainingPurposeType.valueOf(entity.getPurposeType());
        final var purpose = purposeType == TrainingPurposeType.OTHER
                ? TrainingPurpose.other(entity.getPurposeOther())
                : purposeType == TrainingPurposeType.INDIVIDUAL_DEVELOPMENT_PLAN
                        ? TrainingPurpose.individualDevelopmentPlan()
                        : TrainingPurpose.departmentGoals();

        final var attendees = entity.getAttendeeIds() != null
                ? entity.getAttendeeIds().stream().map(EmployeeId::new).collect(Collectors.toSet())
                : Set.<EmployeeId>of();


        return Training.reconstitute(
                new TrainingId(entity.getId()),
                new EmployeeId(entity.getRequestedByEmployeeId()),
                new OrganizationalUnitId(entity.getOrganizationalUnitId()),
                new TrainingName(entity.getName()),
                Cost.of(entity.getCostAmount(), entity.getCostCurrency()),
                new Hours(entity.getHours()),
                purpose,
                new TypeId(entity.getTypeId()),
                TrainingStatus.valueOf(entity.getStatus()),
                managerReview,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                attendees,
                entity.getActive());
    }

    private TrainingEntity toEntity(final Training training) {
        final var entity = new TrainingEntity();
        if (training.id() != null) {
            entity.setId(training.id().value());
        }
        entity.setRequestedByEmployeeId(training.requestedBy().value());
        entity.setOrganizationalUnitId(training.organizationalUnitId().value());
        entity.setName(training.name().value());
        entity.setCostAmount(training.cost().amount());
        entity.setCostCurrency(training.cost().currency());
        entity.setHours(training.hours().value());
        entity.setPurposeType(training.purpose().type().name());
        entity.setPurposeOther(training.purpose().optionalOtherPurpose().orElse(null));
        entity.setTypeId(training.typeId().value());
        entity.setStatus(training.status().name());
        entity.setCreatedAt(training.createdAt());
        entity.setUpdatedAt(training.updatedAt());
        entity.setActive(training.active());

        training.managerReview().ifPresent(review -> {
            entity.setManagerReviewComments(review.comments());
            entity.setManagerReviewModality(review.modality().name());
            entity.setManagerReviewStartDate(review.startDate());
            entity.setManagerReviewEndDate(review.endDate());
            entity.setManagerReviewExternalProviderId(
                    review.optionalExternalProviderId().map(ExternalProviderId::value).orElse(null));
            entity.setManagerReviewReviewedAt(review.reviewedAt());
        });

        if (training.attendees() != null) {
            entity.setAttendeeIds(training.attendees().stream().map(EmployeeId::value).collect(Collectors.toSet()));
        }

        return entity;
    }
}
