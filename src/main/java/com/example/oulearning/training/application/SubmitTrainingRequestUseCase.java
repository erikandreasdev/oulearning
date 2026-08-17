package com.example.oulearning.training.application;

import java.util.UUID;

/**
 * Use case interface for submitting a training request.
 */
public interface SubmitTrainingRequestUseCase {

    UUID execute(SubmitTrainingRequestCommand command);
}
