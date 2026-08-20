package com.example.oulearning.training.domain;

import java.util.UUID;


public record TrainingId(UUID value) {

    public TrainingId {
        TrainingGuard.requireTrainingId(value);
    }

    public static TrainingId of(final UUID value) {
        return new TrainingId(value);
    }

    public static TrainingId fromString(final String value) {
        return new TrainingId(TrainingGuard.requireValidTrainingId(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
