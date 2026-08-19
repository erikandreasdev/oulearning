package com.example.oulearning.training.domain;


public record ExternalProviderName(String value) {

    public ExternalProviderName {
        value = TrainingGuard.requireLengthBetween(
                value, "External provider name", TrainingConstants.MIN_NAME_LENGTH, TrainingConstants.MAX_NAME_LENGTH);
    }

    public static ExternalProviderName of(final String value) {
        return new ExternalProviderName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
