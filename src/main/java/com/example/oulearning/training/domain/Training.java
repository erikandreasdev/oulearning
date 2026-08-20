package com.example.oulearning.training.domain;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.OuId;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public record Training(
        TrainingId id,
        EmployeeId requestedBy,
        OuId ouId,
        TrainingName name,
        Cost cost,
        Hours hours,
        TrainingPurpose purpose,
        TypeId typeId,
        TrainingStatus status,
        ManagerReview rawManagerReview,
        Instant createdAt,
        Instant updatedAt,
        Set<EmployeeId> attendees) {

    public Training {
        id = TrainingGuard.requireNonNull(id, "Training id");
        requestedBy = TrainingGuard.requireNonNull(requestedBy, "RequestedBy employee id");
        ouId = TrainingGuard.requireNonNull(ouId, "Ou id");
        name = TrainingGuard.requireNonNull(name, "Name");
        cost = TrainingGuard.requireNonNull(cost, "Cost");
        hours = TrainingGuard.requireNonNull(hours, "Hours");
        purpose = TrainingGuard.requireNonNull(purpose, "Purpose");
        typeId = TrainingGuard.requireNonNull(typeId, "TypeId");
        status = TrainingGuard.requireNonNull(status, "Status");
        createdAt = TrainingGuard.requireNonNull(createdAt, "CreatedAt");
        updatedAt = TrainingGuard.requireNonNull(updatedAt, "UpdatedAt");
        attendees = (attendees != null) ? Set.copyOf(attendees) : Set.of();
    }

    public static Training of(
            final TrainingId id,
            final EmployeeId requestedBy,
            final OuId ouId,
            final TrainingName name,
            final Cost cost,
            final Hours hours,
            final TrainingPurpose purpose,
            final TypeId typeId,
            final TrainingStatus status,
            final ManagerReview managerReview,
            final Instant createdAt,
            final Instant updatedAt,
            final Set<EmployeeId> attendees) {
        return new Training(
                id,
                requestedBy,
                ouId,
                name,
                cost,
                hours,
                purpose,
                typeId,
                status,
                managerReview,
                createdAt,
                updatedAt,
                attendees);
    }

    public static Training create(
            final TrainingId id,
            final EmployeeId requestedBy,
            final OuId ouId,
            final TrainingName name,
            final Cost cost,
            final Hours hours,
            final TrainingPurpose purpose,
            final TypeId typeId,
            final Instant createdAt) {
        return new Training(
                id,
                requestedBy,
                ouId,
                name,
                cost,
                hours,
                purpose,
                typeId,
                TrainingStatus.REQUESTED,
                null,
                createdAt,
                createdAt,
                Set.of());
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

    @Override
    public String toString() {
        return "Training[id=%s, requestedBy=%s, ouId=%s, name=%s, cost=%s, hours=%s, purpose=%s, typeId=%s, status=%s]"
                .formatted(id, requestedBy, ouId, name, cost, hours, purpose, typeId, status);
    }
}
