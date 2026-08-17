package com.example.oulearning.organization.domain.unit.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Domain exception thrown when OrganizationalUnit invariant rules are violated.
 */
public final class InvalidOuException extends DomainException {

    public InvalidOuException(String message) {
        super(message);
    }
}
