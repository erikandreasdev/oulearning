package com.example.oulearning.training.application;

import com.example.oulearning.training.domain.Cost;
import com.example.oulearning.training.domain.Hours;
import com.example.oulearning.training.domain.IdGenerator;
import com.example.oulearning.training.domain.Training;
import com.example.oulearning.training.domain.TrainingId;
import com.example.oulearning.training.domain.TrainingName;
import com.example.oulearning.training.domain.TrainingPurpose;
import com.example.oulearning.training.domain.TrainingPurposeType;
import com.example.oulearning.training.domain.TrainingRepository;
import com.example.oulearning.training.domain.TypeId;
import java.time.Clock;
import org.springframework.stereotype.Service;

@Service
public class CreateTrainingService implements CreateTrainingUseCase {

    private final TrainingRepository trainingRepository;
    private final IdGenerator idGenerator;
    private final Clock clock;

    public CreateTrainingService(
            final TrainingRepository trainingRepository,
            final IdGenerator idGenerator,
            final Clock clock) {
        this.trainingRepository = trainingRepository;
        this.idGenerator = idGenerator;
        this.clock = clock;
    }

    @Override
    public TrainingId execute(final CreateTrainingCommand command) {
        final var id = TrainingId.of(idGenerator.generate());
        final var purpose = command.purposeType() == TrainingPurposeType.OTHER
                ? TrainingPurpose.other(command.purposeDescription())
                : new TrainingPurpose(command.purposeType(), null);
        final var training = Training.create(
                id,
                command.requestedBy(),
                command.ouId(),
                TrainingName.of(command.name()),
                Cost.of(command.costAmount(), command.currency()),
                Hours.of(command.hours()),
                purpose,
                TypeId.of(command.typeId()),
                clock.instant());
        trainingRepository.save(training);
        return id;
    }
}
