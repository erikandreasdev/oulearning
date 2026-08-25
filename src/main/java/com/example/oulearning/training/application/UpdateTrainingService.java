package com.example.oulearning.training.application;

import com.example.oulearning.training.domain.Cost;
import com.example.oulearning.training.domain.Hours;
import com.example.oulearning.training.domain.TrainingName;
import com.example.oulearning.training.domain.TrainingPurpose;
import com.example.oulearning.training.domain.TrainingPurposeType;
import com.example.oulearning.training.domain.TrainingRepository;
import com.example.oulearning.training.domain.TypeId;
import java.time.Clock;
import org.springframework.stereotype.Service;

@Service
public class UpdateTrainingService implements UpdateTrainingUseCase {

    private final TrainingRepository trainingRepository;
    private final Clock clock;

    public UpdateTrainingService(final TrainingRepository trainingRepository, final Clock clock) {
        this.trainingRepository = trainingRepository;
        this.clock = clock;
    }

    @Override
    public void execute(final UpdateTrainingCommand command) {
        final var training = trainingRepository.findById(command.id())
                .orElseThrow(() -> new TrainingNotFoundException(command.id()));
        final var purpose = command.purposeType() == TrainingPurposeType.OTHER
                ? TrainingPurpose.other(command.purposeDescription())
                : new TrainingPurpose(command.purposeType(), null);
        final var updated = training.updateDetails(
                TrainingName.of(command.name()),
                Cost.of(command.costAmount(), command.currency()),
                Hours.of(command.hours()),
                purpose,
                TypeId.of(command.typeId()),
                clock.instant());
        trainingRepository.save(updated);
    }
}
