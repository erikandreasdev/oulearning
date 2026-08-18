package com.example.oulearning.training.domain.request.exception;

import com.example.oulearning.shared.domain.exception.DomainException;

/**
 * Thrown when attempting an invalid status transition on a training request.
 */
public class IllegalTrainingRequestStateException extends DomainException {

    public IllegalTrainingRequestStateException(String message) {
        super(message);
    }
}
