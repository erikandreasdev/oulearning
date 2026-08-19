package com.example.oulearning.training.domain;

/**
 * Value object representing an external training provider name.
 *
 * @param value the non-blank provider name string
 */
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
