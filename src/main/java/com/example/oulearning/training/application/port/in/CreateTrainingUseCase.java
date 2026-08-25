package com.example.oulearning.training.application.port.in;

import com.example.oulearning.training.domain.model.TrainingId;

public interface CreateTrainingUseCase {
    TrainingId execute(CreateTrainingCommand command);
}
