package com.example.oulearning.training.domain;

import java.util.Objects;

public record ExternalProvider(
        ExternalProviderId id,
        ExternalProviderName name,
        ExternalProviderContact contact,
        boolean active) {

    public ExternalProvider {
        TrainingGuard.requireExternalProviderId(id);
        TrainingGuard.requireExternalProviderName(name);
        TrainingGuard.requireExternalProviderContact(contact);
    }

    public static ExternalProvider create(
            final ExternalProviderId id,
            final ExternalProviderName name,
            final ExternalProviderContact contact) {
        return new ExternalProvider(id, name, contact, true);
    }

    public static ExternalProvider reconstitute(
            final ExternalProviderId id,
            final ExternalProviderName name,
            final ExternalProviderContact contact,
            final boolean active) {
        return new ExternalProvider(id, name, contact, active);
    }

    public static ExternalProvider of(
            final ExternalProviderId id,
            final ExternalProviderName name,
            final ExternalProviderContact contact) {
        return create(id, name, contact);
    }

    public ExternalProvider update(
            final ExternalProviderName newName,
            final ExternalProviderContact newContact) {
        return new ExternalProvider(id, newName, newContact, active);
    }

    public ExternalProvider deactivate() {
        return new ExternalProvider(id, name, contact, false);
    }

    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof final ExternalProvider provider && Objects.equals(id, provider.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
