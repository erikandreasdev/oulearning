package com.example.oulearning.training.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Value object representing a manager's review, scheduling, and provider assignment for a training request.
 *
 * @param comments review comments from the manager
 * @param modality training delivery modality
 * @param startDate scheduled training start timestamp
 * @param endDate scheduled training end timestamp
 * @param externalProvider optional external training provider
 * @param reviewedAt timestamp when the review was conducted
 */
public record ManagerReview(
        String comments,
        Modality modality,
        Instant startDate,
        Instant endDate,
        ExternalProvider externalProvider,
        Instant reviewedAt) {

    public ManagerReview {
        Objects.requireNonNull(modality, "Modality cannot be null");
        Objects.requireNonNull(startDate, "Start date cannot be null");
        Objects.requireNonNull(endDate, "End date cannot be null");
        Objects.requireNonNull(reviewedAt, "ReviewedAt timestamp cannot be null");

        if (endDate.isBefore(startDate)) {
            throw new InvalidTrainingOperationException(
                    "Training end date (" + endDate + ") cannot be before start date (" + startDate + ")");
        }

        comments = (comments != null) ? comments.strip() : "";
    }

    public Optional<ExternalProvider> optionalExternalProvider() {
        return Optional.ofNullable(externalProvider);
    }
}
