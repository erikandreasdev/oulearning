package com.example.oulearning.training.domain.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Exception thrown when a training request is submitted by an unauthorized requester (e.g. not an owner of the OU).
 */
public class UnauthorizedRequesterException extends DomainException {

    public UnauthorizedRequesterException(String message) {
        super(message);
    }
}
