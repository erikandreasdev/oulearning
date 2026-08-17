package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.DomainException;

/**
 * Domain exception thrown when Organization aggregate invariants are violated.
 */
public final class InvalidOrganizationException extends DomainException {

    public InvalidOrganizationException(String message) {
        super(message);
    }
}
