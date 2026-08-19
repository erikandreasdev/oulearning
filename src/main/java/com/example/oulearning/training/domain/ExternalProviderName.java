package com.example.oulearning.training.domain;

/**
 * Value object representing an external training provider's company name.
 *
 * @param value the non-blank provider name
 */
public record ExternalProviderName(String value) {

    public ExternalProviderName {
        if (value == null) {
            throw new InvalidTrainingOperationException("External provider name cannot be null");
        }
        value = value.strip();
        if (value.isBlank()) {
            throw new InvalidTrainingOperationException("External provider name cannot be blank");
        }
    }

    public static ExternalProviderName of(String value) {
        return new ExternalProviderName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
