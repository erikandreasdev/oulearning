package com.example.oulearning.shared.domain;

/**
 * Domain exception thrown when monetary rules or constraints are violated.
 */
public final class InvalidMoneyException extends DomainException {

    public InvalidMoneyException(String message) {
        super(message);
    }
}
