package com.example.oulearning.training.domain;


public record TrainingName(String value) {

    public TrainingName {
        value = TrainingGuard.requireTrainingName(value);
    }

    public static TrainingName of(final String value) {
        return new TrainingName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
