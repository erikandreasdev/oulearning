package com.example.oulearning.training.application.service;

import com.example.oulearning.training.application.port.in.GetTrainingUseCase;
import com.example.oulearning.training.application.exception.TrainingNotFoundException;

import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import org.springframework.stereotype.Service;

@Service
public class GetTrainingService implements GetTrainingUseCase {

    private final TrainingRepository trainingRepository;

    public GetTrainingService(final TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Override
    public Training execute(final TrainingId id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingNotFoundException(id));
    }
}
