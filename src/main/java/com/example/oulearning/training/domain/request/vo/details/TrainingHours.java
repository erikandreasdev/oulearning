package com.example.oulearning.training.domain.request.vo.details;

import com.example.oulearning.training.domain.request.exception.InvalidTrainingRequestException;

/**
 * Value Object representing total hours required for a training program.
 */
public record TrainingHours(int value) {

    public TrainingHours {
        if (value <= 0) {
            throw new InvalidTrainingRequestException("Training hours must be strictly positive, received: " + value);
        }
        if (value > 1000) {
            throw new InvalidTrainingRequestException("Training hours cannot exceed 1000 hours, received: " + value);
        }
    }

    public static TrainingHours of(int value) {
        return new TrainingHours(value);
    }

    @Override
    public String toString() {
        return value + "h";
    }
}
