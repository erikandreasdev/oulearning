package com.example.oulearning.training.application.port.in.usecase;

import com.example.oulearning.training.application.port.in.command.UpdateTrainingCommand;

public interface UpdateTrainingUseCase {
    void execute(UpdateTrainingCommand command);
}
