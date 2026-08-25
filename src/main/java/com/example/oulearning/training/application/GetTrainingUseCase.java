package com.example.oulearning.training.application;

import com.example.oulearning.training.domain.Training;
import com.example.oulearning.training.domain.TrainingId;

public interface GetTrainingUseCase {
    Training execute(TrainingId id);
}
