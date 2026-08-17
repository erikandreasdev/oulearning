package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.DomainException;

/**
 * Domain exception thrown when a corporate key does not meet format constraints.
 */
public final class InvalidCorporateKeyException extends DomainException {

    private final String invalidValue;

    public InvalidCorporateKeyException(String invalidValue, String message) {
        super(message);
        this.invalidValue = invalidValue;
    }

    public String getInvalidValue() {
        return invalidValue;
    }
}
