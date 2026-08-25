package com.example.oulearning.training.application;

import com.example.oulearning.training.domain.Training;
import com.example.oulearning.training.domain.TrainingId;
import com.example.oulearning.training.domain.TrainingRepository;
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
