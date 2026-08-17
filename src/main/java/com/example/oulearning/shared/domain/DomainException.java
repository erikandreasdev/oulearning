package com.example.oulearning.shared.domain;

/**
 * Base exception for all domain-specific business rule and invariant violations across bounded contexts.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
