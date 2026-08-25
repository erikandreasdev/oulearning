package com.example.oulearning.training.application;

import com.example.oulearning.training.domain.TrainingId;

public class TrainingNotFoundException extends RuntimeException {

    private final transient TrainingId trainingId;

    public TrainingNotFoundException(final TrainingId trainingId) {
        super("Training not found with id: %s".formatted(trainingId));
        this.trainingId = trainingId;
    }

    public TrainingId trainingId() {
        return trainingId;
    }
}
