package com.example.oulearning.training.domain.event;

import com.example.oulearning.training.domain.Id;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when an attendee is removed from a training session.
 */
public record AttendeeRemoved(
        Id trainingId,
        com.example.oulearning.organization.domain.employee.Id attendeeId,
        Instant occurredAt) {

    public AttendeeRemoved {
        Objects.requireNonNull(trainingId, "trainingId cannot be null");
        Objects.requireNonNull(attendeeId, "attendeeId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}
