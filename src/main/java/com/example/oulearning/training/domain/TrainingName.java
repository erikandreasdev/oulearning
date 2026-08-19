package com.example.oulearning.training.domain;

/**
 * Value object representing a training program name.
 *
 * @param value the non-blank training name string
 */
public record TrainingName(String value) {

    public TrainingName {
        if (value == null) {
            throw new InvalidTrainingOperationException("Training name cannot be null");
        }
        value = value.strip();
        if (value.isBlank()) {
            throw new InvalidTrainingOperationException("Training name cannot be blank");
        }
    }

    public static TrainingName of(String value) {
        return new TrainingName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
