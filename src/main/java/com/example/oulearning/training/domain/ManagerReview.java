package com.example.oulearning.training.domain;

import com.example.oulearning.training.domain.exception.InvalidTrainingOperationException;
import java.time.Instant;
import java.util.Optional;


public record ManagerReview(
        String comments,
        Modality modality,
        Instant startDate,
        Instant endDate,
        ExternalProvider externalProvider,
        Instant reviewedAt) {

    public ManagerReview {
        comments = TrainingGuard.requireLengthBetween(
                comments, "Comments", TrainingConstants.MIN_COMMENTS_LENGTH, TrainingConstants.MAX_COMMENTS_LENGTH);
        TrainingGuard.requireNonNull(modality, "Modality");
        TrainingGuard.requireNonNull(startDate, "Start date");
        TrainingGuard.requireNonNull(endDate, "End date");
        TrainingGuard.requireNonNull(reviewedAt, "ReviewedAt");

        if (endDate.isBefore(startDate)) {
            throw InvalidTrainingOperationException.invalidDateRange(startDate, endDate);
        }
    }

    public Optional<ExternalProvider> optionalExternalProvider() {
        return Optional.ofNullable(externalProvider);
    }
}
