package com.example.oulearning.training.application.port.in.usecase;

import com.example.oulearning.training.application.port.in.command.RejectTrainingRequestCommand;
/**
 * Use case interface for rejecting a training request.
 */
public interface RejectTrainingRequestUseCase {

    void execute(RejectTrainingRequestCommand command);
}
