package com.example.oulearning.training.domain.model;


public record ExternalProviderName(String value) {

    public ExternalProviderName {
        value = TrainingGuard.requireValidExternalProviderName(value);
    }

    public static ExternalProviderName of(final String value) {
        return new ExternalProviderName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
