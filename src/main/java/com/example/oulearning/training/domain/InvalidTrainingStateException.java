package com.example.oulearning.training.domain;

/**
 * Exception thrown when a training aggregate is in an invalid state for the requested operation.
 */
public final class InvalidTrainingStateException extends TrainingException {

    public InvalidTrainingStateException(String message) {
        super(message);
    }
}
