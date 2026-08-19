package com.example.oulearning.training.domain;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.OuId;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;


public final class Training {

    private final TrainingId id;
    private final EmployeeId requestedBy;
    private final OuId ouId;
    private final TrainingName name;
    private final Cost cost;
    private final Hours hours;
    private final TrainingPurpose purpose;
    private final TypeId typeId;
    private final TrainingStatus status;
    private final ManagerReview managerReview;
    private final Instant createdAt;
    private final Instant updatedAt;
    private final Set<EmployeeId> attendees;

    public Training(
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
        this.id = TrainingGuard.requireNonNull(id, "Training id");
        this.requestedBy = TrainingGuard.requireNonNull(requestedBy, "RequestedBy employee id");
        this.ouId = TrainingGuard.requireNonNull(ouId, "Ou id");
        this.name = TrainingGuard.requireNonNull(name, "Name");
        this.cost = TrainingGuard.requireNonNull(cost, "Cost");
        this.hours = TrainingGuard.requireNonNull(hours, "Hours");
        this.purpose = TrainingGuard.requireNonNull(purpose, "Purpose");
        this.typeId = TrainingGuard.requireNonNull(typeId, "TypeId");
        this.status = TrainingGuard.requireNonNull(status, "Status");
        this.managerReview = managerReview;
        this.createdAt = TrainingGuard.requireNonNull(createdAt, "CreatedAt");
        this.updatedAt = TrainingGuard.requireNonNull(updatedAt, "UpdatedAt");
        this.attendees = (attendees != null) ? Set.copyOf(attendees) : Set.of();
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

    public TrainingId id() {
        return id;
    }

    public EmployeeId requestedBy() {
        return requestedBy;
    }

    public OuId ouId() {
        return ouId;
    }

    public TrainingName name() {
        return name;
    }

    public Cost cost() {
        return cost;
    }

    public Hours hours() {
        return hours;
    }

    public TrainingPurpose purpose() {
        return purpose;
    }

    public TypeId typeId() {
        return typeId;
    }

    public TrainingStatus status() {
        return status;
    }

    public Optional<ManagerReview> managerReview() {
        return Optional.ofNullable(managerReview);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public Set<EmployeeId> attendees() {
        return attendees;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Training training)) return false;
        return Objects.equals(id, training.id);
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
