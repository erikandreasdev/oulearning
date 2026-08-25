package com.example.oulearning.training.application.port.in;

import com.example.oulearning.training.domain.model.TrainingId;

public interface DeleteTrainingUseCase {
    void execute(TrainingId id);
}
