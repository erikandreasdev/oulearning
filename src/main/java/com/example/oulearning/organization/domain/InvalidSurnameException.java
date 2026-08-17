package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.DomainException;

/**
 * Domain exception thrown when a surname does not meet organization domain validation rules.
 */
public final class InvalidSurnameException extends DomainException {

    private final String invalidValue;

    public InvalidSurnameException(String invalidValue, String message) {
        super(message);
        this.invalidValue = invalidValue;
    }

    public String getInvalidValue() {
        return invalidValue;
    }
}
