package com.example.oulearning.training.application;

import com.example.oulearning.training.domain.TrainingId;

public interface DeleteTrainingUseCase {
    void execute(TrainingId id);
}
