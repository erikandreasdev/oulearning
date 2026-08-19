package com.example.oulearning.training.domain;

import java.util.UUID;


public record TrainingId(UUID value) {

    public TrainingId {
        TrainingGuard.requireNonNull(value, "Training id");
    }

    public static TrainingId of(final UUID value) {
        return new TrainingId(value);
    }

    public static TrainingId fromString(final String value) {
        return new TrainingId(TrainingGuard.requireValidUuid(value, "Training id"));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
