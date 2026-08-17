package com.example.oulearning.organization.domain;

import com.example.oulearning.shared.domain.DomainException;

/**
 * Domain exception thrown when organizational unit invariant rules are violated.
 */
public final class InvalidOuException extends DomainException {

    public InvalidOuException(String message) {
        super(message);
    }
}
