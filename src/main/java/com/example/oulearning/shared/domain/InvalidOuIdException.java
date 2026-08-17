package com.example.oulearning.shared.domain;

/**
 * Domain exception thrown when OuId invariant rules are violated.
 */
public final class InvalidOuIdException extends DomainException {

    public InvalidOuIdException(String message) {
        super(message);
    }
}
