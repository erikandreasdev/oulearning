package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.DomainException;

/**
 * Domain exception thrown when employee invariants are violated.
 */
public final class InvalidEmployeeException extends DomainException {

    public InvalidEmployeeException(String message) {
        super(message);
    }
}
