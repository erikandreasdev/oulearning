package com.example.oulearning.training.application;

import com.example.oulearning.training.domain.TrainingId;

public interface CreateTrainingUseCase {
    TrainingId execute(CreateTrainingCommand command);
}
