package com.example.oulearning.training.domain;

/**
 * Value object representing training duration in hours.
 *
 * @param value the positive number of hours
 */
public record Hours(int value) {

    public Hours {
        value = TrainingGuard.requireHoursAtLeast(value, TrainingConstants.MIN_HOURS);
    }

    public static Hours of(final int value) {
        return new Hours(value);
    }

    @Override
    public String toString() {
        return "%d".formatted(value);
    }
}
