package com.example.oulearning.training.domain;


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
