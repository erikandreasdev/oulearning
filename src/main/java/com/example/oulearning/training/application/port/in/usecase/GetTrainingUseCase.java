package com.example.oulearning.training.application.port.in.usecase;

import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingId;

public interface GetTrainingUseCase {
    Training execute(TrainingId id);
}
