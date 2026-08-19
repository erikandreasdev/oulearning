package com.example.oulearning.training.domain;

import com.example.oulearning.organization.domain.employee.Email;

/**
 * Value object representing contact information for an external training provider.
 *
 * @param email the contact email
 * @param phone the contact phone number
 */
public record ExternalProviderContact(Email email, Phone phone) {

    public ExternalProviderContact {
        TrainingGuard.requireNonNull(email, "Email");
        TrainingGuard.requireNonNull(phone, "Phone");
    }

    public static ExternalProviderContact of(final Email email, final Phone phone) {
        return new ExternalProviderContact(email, phone);
    }
}
