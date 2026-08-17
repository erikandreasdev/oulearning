package com.example.oulearning.shared.domain;

/**
 * Domain exception thrown when a phone number does not meet domain validation rules.
 */
public final class InvalidPhoneException extends DomainException {

    private final String invalidValue;

    public InvalidPhoneException(String invalidValue, String message) {
        super(message);
        this.invalidValue = invalidValue;
    }

    public String getInvalidValue() {
        return invalidValue;
    }
}
