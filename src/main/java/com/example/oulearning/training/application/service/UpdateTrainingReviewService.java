package com.example.oulearning.training.application.service;

import com.example.oulearning.training.application.exception.TrainingNotFoundException;
import com.example.oulearning.training.application.port.in.command.UpdateTrainingReviewCommand;
import com.example.oulearning.training.application.port.in.usecase.UpdateTrainingReviewUseCase;
import com.example.oulearning.training.domain.model.ManagerReview;
import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import java.time.Clock;
import org.springframework.stereotype.Service;

@Service
public class UpdateTrainingReviewService implements UpdateTrainingReviewUseCase {

    private final TrainingRepository trainingRepository;
    private final Clock clock;

    public UpdateTrainingReviewService(final TrainingRepository trainingRepository, final Clock clock) {
        this.trainingRepository = trainingRepository;
        this.clock = clock;
    }

    @Override
    public Training execute(final UpdateTrainingReviewCommand command) {
        final var training = trainingRepository.findById(command.trainingId())
                .orElseThrow(() -> new TrainingNotFoundException(command.trainingId()));

        final var managerReview = new ManagerReview(
                command.comments(),
                command.modality(),
                command.startDate(),
                command.endDate(),
                command.externalProviderId(),
                command.reviewedAt());

        final var updatedTraining = training.updateManagerReview(managerReview, clock.instant());
        trainingRepository.save(updatedTraining);
        return updatedTraining;
    }
}
