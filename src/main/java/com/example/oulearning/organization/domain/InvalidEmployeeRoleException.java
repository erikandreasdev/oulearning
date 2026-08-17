package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.DomainException;

/**
 * Domain exception thrown when an invalid employee role is provided.
 */
public final class InvalidEmployeeRoleException extends DomainException {

    private final String invalidValue;

    public InvalidEmployeeRoleException(String invalidValue, String message) {
        super(message);
        this.invalidValue = invalidValue;
    }

    public String getInvalidValue() {
        return invalidValue;
    }
}
