package com.example.oulearning.training.domain;

import java.util.UUID;

/**
 * Value object representing a training identifier.
 *
 * @param value the UUID value
 */
public record TrainingId(UUID value) {

    public TrainingId {
        if (value == null) {
            throw new InvalidTrainingOperationException("Training id cannot be null");
        }
    }

    public static TrainingId of(UUID value) {
        return new TrainingId(value);
    }

    public static TrainingId fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidTrainingOperationException("Training id string cannot be null or blank");
        }
        try {
            return new TrainingId(UUID.fromString(value.strip()));
        } catch (IllegalArgumentException e) {
            throw new InvalidTrainingOperationException("Invalid UUID format: " + value);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
