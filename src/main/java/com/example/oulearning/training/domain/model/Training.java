package com.example.oulearning.training.domain.model;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public record Training(
        TrainingId id,
        EmployeeId requestedBy,
        OrganizationalUnitId organizationalUnitId,
        TrainingName name,
        Cost cost,
        Hours hours,
        TrainingPurpose purpose,
        TypeId typeId,
        TrainingStatus status,
        ManagerReview rawManagerReview,
        Instant createdAt,
        Instant updatedAt,
        Set<EmployeeId> attendees,
        boolean active) {

    public Training {
        TrainingGuard.requireTrainingId(id);
        TrainingGuard.requireRequestedBy(requestedBy);
        TrainingGuard.requireOrganizationalUnitId(organizationalUnitId);
        TrainingGuard.requireTrainingName(name);
        TrainingGuard.requireCost(cost);
        TrainingGuard.requireHours(hours);
        TrainingGuard.requirePurpose(purpose);
        TrainingGuard.requireTypeId(typeId);
        TrainingGuard.requireStatus(status);
        TrainingGuard.requireCreatedAt(createdAt);
        TrainingGuard.requireUpdatedAt(updatedAt);
        attendees = (attendees != null) ? Set.copyOf(attendees) : Set.of();
    }

    public static Training create(
            final TrainingId id,
            final EmployeeId requestedBy,
            final OrganizationalUnitId organizationalUnitId,
            final TrainingName name,
            final Cost cost,
            final Hours hours,
            final TrainingPurpose purpose,
            final TypeId typeId,
            final Instant now) {
        return new Training(
                id,
                requestedBy,
                organizationalUnitId,
                name,
                cost,
                hours,
                purpose,
                typeId,
                TrainingStatus.REQUESTED,
                null,
                now,
                now,
                Set.of(),
                true);
    }

    public static Training reconstitute(
            final TrainingId id,
            final EmployeeId requestedBy,
            final OrganizationalUnitId organizationalUnitId,
            final TrainingName name,
            final Cost cost,
            final Hours hours,
            final TrainingPurpose purpose,
            final TypeId typeId,
            final TrainingStatus status,
            final ManagerReview managerReview,
            final Instant createdAt,
            final Instant updatedAt,
            final Set<EmployeeId> attendees,
            final boolean active) {
        return new Training(
                id,
                requestedBy,
                organizationalUnitId,
                name,
                cost,
                hours,
                purpose,
                typeId,
                status,
                managerReview,
                createdAt,
                updatedAt,
                attendees,
                active);
    }

    public Training approve(final ManagerReview review, final Instant now) {
        TrainingGuard.requireReviewedAt(now);
        return new Training(
                id,
                requestedBy,
                organizationalUnitId,
                name,
                cost,
                hours,
                purpose,
                typeId,
                TrainingStatus.APPROVED,
                review,
                createdAt,
                now,
                attendees,
                active);
    }

    public Training reject(final ManagerReview review, final Instant now) {
        TrainingGuard.requireReviewedAt(now);
        return new Training(
                id,
                requestedBy,
                organizationalUnitId,
                name,
                cost,
                hours,
                purpose,
                typeId,
                TrainingStatus.REJECTED,
                review,
                createdAt,
                now,
                attendees,
                active);
    }

    public Training addAttendee(final EmployeeId attendee, final Instant now) {
        TrainingGuard.requireAttendee(attendee);
        TrainingGuard.requireUpdatedAt(now);
        final var updated = new HashSet<>(attendees);
        updated.add(attendee);
        return new Training(
                id,
                requestedBy,
                organizationalUnitId,
                name,
                cost,
                hours,
                purpose,
                typeId,
                status,
                rawManagerReview,
                createdAt,
                now,
                updated,
                active);
    }

    public Training updateDetails(
            final TrainingName newName,
            final Cost newCost,
            final Hours newHours,
            final TrainingPurpose newPurpose,
            final TypeId newTypeId,
            final Instant now) {
        TrainingGuard.requireTrainingName(newName);
        TrainingGuard.requireCost(newCost);
        TrainingGuard.requireHours(newHours);
        TrainingGuard.requirePurpose(newPurpose);
        TrainingGuard.requireTypeId(newTypeId);
        TrainingGuard.requireUpdatedAt(now);
        return new Training(
                id,
                requestedBy,
                organizationalUnitId,
                newName,
                newCost,
                newHours,
                newPurpose,
                newTypeId,
                status,
                rawManagerReview,
                createdAt,
                now,
                attendees,
                active);
    }

    public Training deactivate() {
        return new Training(
                id,
                requestedBy,
                organizationalUnitId,
                name,
                cost,
                hours,
                purpose,
                typeId,
                status,
                rawManagerReview,
                createdAt,
                updatedAt,
                attendees,
                false);
    }

    public Optional<ManagerReview> managerReview() {
        return Optional.ofNullable(rawManagerReview);
    }

    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof final Training training && Objects.equals(id, training.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
