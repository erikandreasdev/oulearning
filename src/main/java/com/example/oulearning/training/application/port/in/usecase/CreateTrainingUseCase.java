package com.example.oulearning.training.application.port.in.usecase;

import com.example.oulearning.training.application.port.in.command.CreateTrainingCommand;
import com.example.oulearning.training.domain.model.TrainingId;

public interface CreateTrainingUseCase {
    TrainingId execute(CreateTrainingCommand command);
}
