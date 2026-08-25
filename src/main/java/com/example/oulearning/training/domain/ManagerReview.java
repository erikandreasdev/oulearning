package com.example.oulearning.training.domain;

import java.time.Instant;
import java.util.Optional;

public record ManagerReview(
        String comments,
        Modality modality,
        Instant startDate,
        Instant endDate,
        ExternalProviderId externalProviderId,
        Instant reviewedAt) {

    public ManagerReview {
        comments = TrainingGuard.requireValidComments(comments);
        TrainingGuard.requireModality(modality);
        TrainingGuard.requireStartDate(startDate);
        TrainingGuard.requireEndDate(endDate);
        TrainingGuard.requireReviewedAt(reviewedAt);
        TrainingGuard.requireDateRange(startDate, endDate);
    }

    public Optional<ExternalProviderId> optionalExternalProviderId() {
        return Optional.ofNullable(externalProviderId);
    }
}
