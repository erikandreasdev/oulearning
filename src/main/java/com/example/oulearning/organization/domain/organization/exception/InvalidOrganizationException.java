package com.example.oulearning.organization.domain.organization.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Domain exception thrown when Organization aggregate invariants are violated.
 */
public final class InvalidOrganizationException extends DomainException {

    public InvalidOrganizationException(String message) {
        super(message);
    }
}
