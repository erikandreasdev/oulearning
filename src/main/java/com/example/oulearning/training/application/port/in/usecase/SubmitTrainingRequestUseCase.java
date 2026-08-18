package com.example.oulearning.training.application.port.in.usecase;

import java.util.UUID;
import com.example.oulearning.training.application.port.in.command.SubmitTrainingRequestCommand;

/**
 * Use case interface for submitting a training request.
 */
public interface SubmitTrainingRequestUseCase {

    UUID execute(SubmitTrainingRequestCommand command);
}
