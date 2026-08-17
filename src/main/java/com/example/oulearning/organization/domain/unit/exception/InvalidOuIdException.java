package com.example.oulearning.organization.domain.unit.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Domain exception thrown when an invalid OU identifier is encountered.
 */
public final class InvalidOuIdException extends DomainException {

    public InvalidOuIdException(String message) {
        super(message);
    }
}
