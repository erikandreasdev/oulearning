package com.example.oulearning.training.application.service;

import com.example.oulearning.organization.application.employee.port.in.usecase.GetEmployeeUseCase;
import com.example.oulearning.training.application.exception.TrainingNotFoundException;
import com.example.oulearning.training.application.port.in.model.AttendeeDetailsDto;
import com.example.oulearning.training.application.port.in.model.TrainingDetailedViewDto;
import com.example.oulearning.training.application.port.in.usecase.GetTrainingDetailsUseCase;
import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetTrainingDetailsService implements GetTrainingDetailsUseCase {

    private final TrainingRepository trainingRepository;
    private final GetEmployeeUseCase getEmployeeUseCase;

    public GetTrainingDetailsService(
            final TrainingRepository trainingRepository,
            final GetEmployeeUseCase getEmployeeUseCase) {
        this.trainingRepository = trainingRepository;
        this.getEmployeeUseCase = getEmployeeUseCase;
    }

    @Override
    public TrainingDetailedViewDto execute(final TrainingId id) {
        final var training = trainingRepository.findById(id)
                .orElseThrow(() -> new TrainingNotFoundException(id));

        final var requestedByEmployee = getEmployeeUseCase.execute(training.requestedBy());
        final var attendeeDtos = new ArrayList<AttendeeDetailsDto>();
        for (final var attendeeId : training.attendees()) {
            final var employee = getEmployeeUseCase.execute(attendeeId);
            attendeeDtos.add(new AttendeeDetailsDto(
                    employee.id(),
                    employee.fullName().formatted(),
                    employee.email().value()));
        }

        return new TrainingDetailedViewDto(
                training.id(),
                training.name(),
                training.cost(),
                requestedByEmployee.fullName().formatted(),
                training.purpose(),
                training.typeId(),
                training.hours(),
                List.copyOf(attendeeDtos));
    }
}
