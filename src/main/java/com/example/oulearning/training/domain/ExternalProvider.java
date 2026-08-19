package com.example.oulearning.training.domain;

/**
 * Value object representing an external training provider entity.
 *
 * @param name the provider name
 * @param contact the provider contact details
 */
public record ExternalProvider(ExternalProviderName name, ExternalProviderContact contact) {

    public ExternalProvider {
        TrainingGuard.requireNonNull(name, "ExternalProviderName");
        TrainingGuard.requireNonNull(contact, "ExternalProviderContact");
    }

    public static ExternalProvider of(final ExternalProviderName name, final ExternalProviderContact contact) {
        return new ExternalProvider(name, contact);
    }
}
