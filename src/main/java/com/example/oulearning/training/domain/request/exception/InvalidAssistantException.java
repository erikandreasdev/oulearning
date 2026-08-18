package com.example.oulearning.training.domain.request.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Exception thrown when an assistant is invalid (e.g. not a member of the requested OU).
 */
public class InvalidAssistantException extends DomainException {

    public InvalidAssistantException(String message) {
        super(message);
    }
}
