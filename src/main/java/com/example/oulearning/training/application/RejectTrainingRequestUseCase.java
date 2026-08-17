package com.example.oulearning.training.application;

/**
 * Use case interface for rejecting a training request.
 */
public interface RejectTrainingRequestUseCase {

    void execute(RejectTrainingRequestCommand command);
}
