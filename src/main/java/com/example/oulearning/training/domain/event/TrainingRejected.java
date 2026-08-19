package com.example.oulearning.training.domain.event;

import com.example.oulearning.training.domain.Id;
import com.example.oulearning.training.domain.ManagerReview;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a training request is rejected by a manager.
 */
public record TrainingRejected(Id trainingId, ManagerReview review, Instant occurredAt) {

    public TrainingRejected {
        Objects.requireNonNull(trainingId, "trainingId cannot be null");
        Objects.requireNonNull(review, "review cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}
