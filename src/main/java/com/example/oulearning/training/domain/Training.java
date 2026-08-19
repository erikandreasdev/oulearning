package com.example.oulearning.training.domain;

import com.example.oulearning.training.domain.event.AttendeeAdded;
import com.example.oulearning.training.domain.event.AttendeeRemoved;
import com.example.oulearning.training.domain.event.TrainingApproved;
import com.example.oulearning.training.domain.event.TrainingRejected;
import com.example.oulearning.training.domain.event.TrainingRequested;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Aggregate root representing a Training request and lifecycle.
 */
public final class Training {

    private final Id id;
    private final com.example.oulearning.organization.domain.employee.Id requestedBy;
    private final com.example.oulearning.organization.domain.hierarchy.Id ouId;
    private final TrainingName name;
    private final Cost cost;
    private final Hours hours;
    private final TrainingPurpose purpose;
    private final TypeId typeId;
    private TrainingStatus status;
    private ManagerReview managerReview;
    private final Instant createdAt;
    private Instant updatedAt;
    private final Set<com.example.oulearning.organization.domain.employee.Id> attendees = new HashSet<>();
    private final List<Object> domainEvents = new ArrayList<>();

    private Training(
            Id id,
            com.example.oulearning.organization.domain.employee.Id requestedBy,
            com.example.oulearning.organization.domain.hierarchy.Id ouId,
            TrainingName name,
            Cost cost,
            Hours hours,
            TrainingPurpose purpose,
            TypeId typeId,
            TrainingStatus status,
            ManagerReview managerReview,
            Instant createdAt,
            Instant updatedAt,
            Set<com.example.oulearning.organization.domain.employee.Id> attendees) {
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
        if (attendees != null) {
            this.attendees.addAll(attendees);
        }
    }

    /**
     * Factory method to request a new training.
     */
    public static Training request(
            Id id,
            com.example.oulearning.organization.domain.employee.Id requestedBy,
            com.example.oulearning.organization.domain.hierarchy.Id ouId,
            TrainingName name,
            Cost cost,
            Hours hours,
            TrainingPurpose purpose,
            TypeId typeId,
            Instant createdAt) {
        Objects.requireNonNull(createdAt, "createdAt cannot be null");
        Training training = new Training(
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
        training.registerEvent(new TrainingRequested(id, requestedBy, ouId, name, cost, hours, purpose, typeId, createdAt));
        return training;
    }

    /**
     * Reconstitutes an existing {@link Training} from persistence.
     */
    public static Training reconstitute(
            Id id,
            com.example.oulearning.organization.domain.employee.Id requestedBy,
            com.example.oulearning.organization.domain.hierarchy.Id ouId,
            TrainingName name,
            Cost cost,
            Hours hours,
            TrainingPurpose purpose,
            TypeId typeId,
            TrainingStatus status,
            ManagerReview managerReview,
            Instant createdAt,
            Instant updatedAt,
            Set<com.example.oulearning.organization.domain.employee.Id> attendees) {
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

    public void approve(ManagerReview review, Instant timestamp) {
        Objects.requireNonNull(review, "ManagerReview cannot be null");
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        if (this.status != TrainingStatus.REQUESTED) {
            throw new InvalidTrainingStateException("Cannot approve training in " + this.status + " status");
        }
        this.status = TrainingStatus.APPROVED;
        this.managerReview = review;
        this.updatedAt = timestamp;
        registerEvent(new TrainingApproved(this.id, review, timestamp));
    }

    public void reject(ManagerReview review, Instant timestamp) {
        Objects.requireNonNull(review, "ManagerReview cannot be null");
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        if (this.status != TrainingStatus.REQUESTED) {
            throw new InvalidTrainingStateException("Cannot reject training in " + this.status + " status");
        }
        this.status = TrainingStatus.REJECTED;
        this.managerReview = review;
        this.updatedAt = timestamp;
        registerEvent(new TrainingRejected(this.id, review, timestamp));
    }

    public void addAttendee(com.example.oulearning.organization.domain.employee.Id attendeeId, Instant occurredAt) {
        Objects.requireNonNull(attendeeId, "AttendeeId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (this.attendees.add(attendeeId)) {
            registerEvent(new AttendeeAdded(this.id, attendeeId, occurredAt));
        }
    }

    public void removeAttendee(com.example.oulearning.organization.domain.employee.Id attendeeId, Instant occurredAt) {
        Objects.requireNonNull(attendeeId, "AttendeeId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (this.attendees.remove(attendeeId)) {
            registerEvent(new AttendeeRemoved(this.id, attendeeId, occurredAt));
        }
    }

    public Id id() {
        return id;
    }

    public com.example.oulearning.organization.domain.employee.Id requestedBy() {
        return requestedBy;
    }

    public com.example.oulearning.organization.domain.hierarchy.Id ouId() {
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

    public Set<com.example.oulearning.organization.domain.employee.Id> attendees() {
        return Collections.unmodifiableSet(attendees);
    }

    private void registerEvent(Object event) {
        this.domainEvents.add(event);
    }

    public List<Object> pullDomainEvents() {
        List<Object> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return Collections.unmodifiableList(events);
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
