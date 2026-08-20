package com.example.oulearning.training.domain;

import com.example.oulearning.organization.domain.employee.Email;


public record ExternalProviderContact(Email email, Phone phone) {

    public ExternalProviderContact {
        TrainingGuard.requireContactEmail(email);
        TrainingGuard.requireContactPhone(phone);
    }

    public static ExternalProviderContact of(final Email email, final Phone phone) {
        return new ExternalProviderContact(email, phone);
    }
}
