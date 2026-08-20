package com.example.oulearning.training.domain;

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
        comments = TrainingGuard.requireComments(comments);
        TrainingGuard.requireModality(modality);
        TrainingGuard.requireStartDate(startDate);
        TrainingGuard.requireEndDate(endDate);
        TrainingGuard.requireReviewedAt(reviewedAt);
        TrainingGuard.requireDateRange(startDate, endDate);
    }

    public Optional<ExternalProvider> optionalExternalProvider() {
        return Optional.ofNullable(externalProvider);
    }
}
