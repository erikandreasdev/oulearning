package com.example.oulearning.training.domain.exception;

/** Base sealed exception for all domain errors within the training bounded context. */
public abstract sealed class TrainingException extends RuntimeException
    permits InvalidTrainingOperationException, InvalidTrainingStateException {

  protected TrainingException(String message) {
    super(message);
  }

  protected TrainingException(String message, Throwable cause) {
    super(message, cause);
  }
}
