package com.example.oulearning.training.domain;

import java.util.UUID;

/**
 * Value object representing a training identifier.
 *
 * @param value the UUID value
 */
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
