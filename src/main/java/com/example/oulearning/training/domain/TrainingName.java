package com.example.oulearning.training.domain;

/**
 * Value object representing a training name.
 *
 * @param value the non-blank name string
 */
public record TrainingName(String value) {

    public TrainingName {
        value = TrainingGuard.requireLengthBetween(
                value, "Training name", TrainingConstants.MIN_NAME_LENGTH, TrainingConstants.MAX_NAME_LENGTH);
    }

    public static TrainingName of(final String value) {
        return new TrainingName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
