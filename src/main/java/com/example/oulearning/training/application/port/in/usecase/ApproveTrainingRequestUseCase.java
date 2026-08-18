package com.example.oulearning.training.application.port.in.usecase;

import com.example.oulearning.training.application.port.in.command.ApproveTrainingRequestCommand;
/**
 * Use case interface for approving a training request.
 */
public interface ApproveTrainingRequestUseCase {

    void execute(ApproveTrainingRequestCommand command);
}
