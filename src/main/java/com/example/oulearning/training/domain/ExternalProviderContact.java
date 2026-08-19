package com.example.oulearning.training.domain;

import com.example.oulearning.organization.domain.employee.Email;
import java.util.Objects;

/**
 * Value object representing an external training provider's contact details.
 *
 * @param email the provider's email address
 * @param phone the provider's phone number
 */
public record ExternalProviderContact(Email email, Phone phone) {

    public ExternalProviderContact {
        Objects.requireNonNull(email, "External provider email cannot be null");
        Objects.requireNonNull(phone, "External provider phone cannot be null");
    }

    public static ExternalProviderContact of(Email email, Phone phone) {
        return new ExternalProviderContact(email, phone);
    }
}
