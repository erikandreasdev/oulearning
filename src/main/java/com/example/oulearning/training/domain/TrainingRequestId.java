package com.example.oulearning.training.domain;

import com.example.oulearning.training.domain.exception.InvalidTrainingRequestException;
import java.util.Objects;
import java.util.UUID;

/**
 * Strongly-typed identifier for {@link TrainingRequest}.
 */
public record TrainingRequestId(UUID value) {

    public TrainingRequestId {
        if (value == null) {
            throw new InvalidTrainingRequestException("TrainingRequestId value cannot be null");
        }
    }

    public static TrainingRequestId of(UUID value) {
        return new TrainingRequestId(value);
    }

    public static TrainingRequestId of(String value) {
        Objects.requireNonNull(value, "TrainingRequestId string cannot be null");
        return new TrainingRequestId(UUID.fromString(value));
    }

    public static TrainingRequestId random() {
        return new TrainingRequestId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
