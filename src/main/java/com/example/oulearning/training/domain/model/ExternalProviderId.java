package com.example.oulearning.training.domain.model;

public record ExternalProviderId(long value) {

    public ExternalProviderId {
        TrainingGuard.requirePositiveExternalProviderId(value);
    }

    public static ExternalProviderId of(final long value) {
        return new ExternalProviderId(value);
    }

    public static ExternalProviderId fromString(final String value) {
        return new ExternalProviderId(TrainingGuard.requireValidExternalProviderId(value));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
