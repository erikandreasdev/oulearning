package com.example.oulearning.training.domain.exception;

/** Exception thrown when a lifecycle transition violates training state rules. */
public final class InvalidTrainingStateException extends TrainingException {

  public InvalidTrainingStateException(String message) {
    super(message);
  }
}
