package com.example.oulearning.training.domain.exception;

public final class InvalidTrainingStateException extends TrainingException {

    public InvalidTrainingStateException(final String message) {
        super(message);
    }

    public InvalidTrainingStateException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
