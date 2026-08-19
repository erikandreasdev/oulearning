package com.example.oulearning.training.domain;

/**
 * Value object representing training duration in hours.
 *
 * @param value the positive number of hours
 */
public record Hours(int value) {

    public Hours {
        if (value <= 0) {
            throw new InvalidTrainingOperationException("Training hours must be strictly positive: " + value);
        }
    }

    public static Hours of(int value) {
        return new Hours(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
