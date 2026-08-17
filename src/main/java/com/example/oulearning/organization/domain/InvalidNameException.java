package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.DomainException;

/**
 * Domain exception thrown when a name does not meet organization domain validation rules.
 */
public final class InvalidNameException extends DomainException {

    private final String invalidValue;

    public InvalidNameException(String invalidValue, String message) {
        super(message);
        this.invalidValue = invalidValue;
    }

    public String getInvalidValue() {
        return invalidValue;
    }
}
