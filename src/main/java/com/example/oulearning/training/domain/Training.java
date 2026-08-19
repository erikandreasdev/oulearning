package com.example.oulearning.training.domain;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.OuId;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Domain object representing a Training.
 */
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
            TrainingId id,
            EmployeeId requestedBy,
            OuId ouId,
            TrainingName name,
            Cost cost,
            Hours hours,
            TrainingPurpose purpose,
            TypeId typeId,
            TrainingStatus status,
            ManagerReview managerReview,
            Instant createdAt,
            Instant updatedAt,
            Set<EmployeeId> attendees) {
        this.id = Objects.requireNonNull(id, "Training id cannot be null");
        this.requestedBy = Objects.requireNonNull(requestedBy, "RequestedBy employee id cannot be null");
        this.ouId = Objects.requireNonNull(ouId, "Ou id cannot be null");
        this.name = Objects.requireNonNull(name, "Training name cannot be null");
        this.cost = Objects.requireNonNull(cost, "Cost cannot be null");
        this.hours = Objects.requireNonNull(hours, "Hours cannot be null");
        this.purpose = Objects.requireNonNull(purpose, "TrainingPurpose cannot be null");
        this.typeId = Objects.requireNonNull(typeId, "TypeId cannot be null");
        this.status = Objects.requireNonNull(status, "TrainingStatus cannot be null");
        this.managerReview = managerReview;
        this.createdAt = Objects.requireNonNull(createdAt, "CreatedAt timestamp cannot be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "UpdatedAt timestamp cannot be null");
        this.attendees = (attendees != null) ? Set.copyOf(attendees) : Set.of();
    }

    public static Training of(
            TrainingId id,
            EmployeeId requestedBy,
            OuId ouId,
            TrainingName name,
            Cost cost,
            Hours hours,
            TrainingPurpose purpose,
            TypeId typeId,
            TrainingStatus status,
            ManagerReview managerReview,
            Instant createdAt,
            Instant updatedAt,
            Set<EmployeeId> attendees) {
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
    public boolean equals(Object o) {
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
        return "Training[id=" + id + ", name=" + name + ", status=" + status + "]";
    }
}
