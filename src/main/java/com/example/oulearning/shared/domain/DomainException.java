package com.example.oulearning.shared.domain;

/**
 * Base sealed exception for all domain-specific business rule and invariant violations.
 */
public abstract sealed class DomainException extends RuntimeException
        permits InvalidEmailException, InvalidPhoneException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
