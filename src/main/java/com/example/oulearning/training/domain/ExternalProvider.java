package com.example.oulearning.training.domain;

import java.util.Objects;

/**
 * Value object representing an external training provider.
 *
 * @param name the provider's name
 * @param contact the provider's contact information
 */
public record ExternalProvider(ExternalProviderName name, ExternalProviderContact contact) {

    public ExternalProvider {
        Objects.requireNonNull(name, "External provider name cannot be null");
        Objects.requireNonNull(contact, "External provider contact cannot be null");
    }

    public static ExternalProvider of(ExternalProviderName name, ExternalProviderContact contact) {
        return new ExternalProvider(name, contact);
    }
}
