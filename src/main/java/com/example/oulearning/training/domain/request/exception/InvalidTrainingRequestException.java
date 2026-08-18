package com.example.oulearning.training.domain.request.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Exception thrown when a training request violates domain invariants.
 */
public class InvalidTrainingRequestException extends DomainException {

    public InvalidTrainingRequestException(String message) {
        super(message);
    }
}
