package com.example.oulearning.training.application;

import com.example.oulearning.training.domain.TrainingId;
import com.example.oulearning.training.domain.TrainingRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteTrainingService implements DeleteTrainingUseCase {

    private final TrainingRepository trainingRepository;

    public DeleteTrainingService(final TrainingRepository trainingRepository) {
        this.trainingRepository = trainingRepository;
    }

    @Override
    public void execute(final TrainingId id) {
        final var training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingNotFoundException(id));
        trainingRepository.save(training.deactivate());
    }
}
