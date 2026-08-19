package com.example.oulearning.training.domain;

/**
 * Exception thrown when a training parameter, date range, or invariant is violated.
 */
public final class InvalidTrainingOperationException extends TrainingException {

    public InvalidTrainingOperationException(String message) {
        super(message);
    }
}
