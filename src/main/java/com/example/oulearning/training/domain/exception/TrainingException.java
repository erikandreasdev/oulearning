package com.example.oulearning.training.domain.exception;

public abstract sealed class TrainingException extends RuntimeException
        permits InvalidTrainingOperationException, InvalidTrainingStateException {

    protected TrainingException(final String message) {
        super(message);
    }

    protected TrainingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
