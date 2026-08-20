package com.example.oulearning.training.domain;


public record ExternalProvider(ExternalProviderName name, ExternalProviderContact contact) {

    public ExternalProvider {
        TrainingGuard.requireExternalProviderName(name);
        TrainingGuard.requireExternalProviderContact(contact);
    }

    public static ExternalProvider of(final ExternalProviderName name, final ExternalProviderContact contact) {
        return new ExternalProvider(name, contact);
    }
}
