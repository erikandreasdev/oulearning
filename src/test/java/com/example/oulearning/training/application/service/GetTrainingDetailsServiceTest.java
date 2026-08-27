package com.example.oulearning.training.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.application.employee.port.in.usecase.GetEmployeeUseCase;
import com.example.oulearning.organization.domain.employee.model.EmployeeTestFactory;
import com.example.oulearning.training.domain.model.TrainingTestFactory;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetTrainingDetailsServiceTest {

    private final TrainingRepository trainingRepository = mock(TrainingRepository.class);
    private final GetEmployeeUseCase getEmployeeUseCase = mock(GetEmployeeUseCase.class);
    private final GetTrainingDetailsService service = new GetTrainingDetailsService(trainingRepository, getEmployeeUseCase);

    @Test
    @DisplayName("given training with requester and attendees, when executing, then return detailed view")
    void givenTrainingWithRequesterAndAttendees_whenExecuting_thenReturnDetailedView() {
        // given
        final var training = TrainingTestFactory.randomTraining();
        final var requester = EmployeeTestFactory.randomEmployee();
        when(trainingRepository.findById(training.id())).thenReturn(Optional.of(training));
        when(getEmployeeUseCase.execute(training.requestedBy())).thenReturn(requester);

        // when
        final var result = service.execute(training.id());

        // then
        assertThat(result.id()).isEqualTo(training.id());
        assertThat(result.name()).isEqualTo(training.name());
        assertThat(result.requestedByName()).isEqualTo(requester.fullName().formatted());
    }
}
