package com.example.oulearning.training.domain.model;


public record TrainingId(long value) {

    public TrainingId {
        TrainingGuard.requirePositiveTrainingId(value);
    }

    public static TrainingId of(final long value) {
        return new TrainingId(value);
    }

    public static TrainingId fromString(final String value) {
        return new TrainingId(TrainingGuard.requireValidTrainingId(value));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
