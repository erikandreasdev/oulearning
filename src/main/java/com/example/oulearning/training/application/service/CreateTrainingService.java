package com.example.oulearning.training.application.service;

import com.example.oulearning.training.application.port.in.CreateTrainingCommand;
import com.example.oulearning.training.application.port.in.CreateTrainingUseCase;

import com.example.oulearning.training.domain.model.Cost;
import com.example.oulearning.training.domain.model.Hours;
import com.example.oulearning.training.domain.model.IdGenerator;
import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.model.TrainingName;
import com.example.oulearning.training.domain.model.TrainingPurpose;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import com.example.oulearning.training.domain.model.TypeId;
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
