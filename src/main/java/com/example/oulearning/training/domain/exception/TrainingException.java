package com.example.oulearning.training.domain.exception;


public abstract sealed class TrainingException extends RuntimeException
    permits InvalidTrainingOperationException, InvalidTrainingStateException {

  protected TrainingException(String message) {
    super(message);
  }

  protected TrainingException(String message, Throwable cause) {
    super(message, cause);
  }
}
