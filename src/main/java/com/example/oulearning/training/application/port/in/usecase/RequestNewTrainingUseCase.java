package com.example.oulearning.training.application.port.in.usecase;

import com.example.oulearning.training.application.port.in.command.RequestNewTrainingCommand;
import com.example.oulearning.training.domain.model.Training;

public interface RequestNewTrainingUseCase {
    Training execute(RequestNewTrainingCommand command);
}
