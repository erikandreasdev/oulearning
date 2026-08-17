package com.example.oulearning.training.domain;

import com.example.oulearning.training.domain.exception.InvalidTrainingRequestException;

/**
 * Value Object representing the name of a training program.
 */
public record TrainingName(String value) {

    public TrainingName {
        if (value == null || value.isBlank()) {
            throw new InvalidTrainingRequestException("Training name cannot be null or blank");
        }
        if (value.length() > 200) {
            throw new InvalidTrainingRequestException("Training name cannot exceed 200 characters");
        }
        value = value.trim();
    }

    public static TrainingName of(String value) {
        return new TrainingName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
