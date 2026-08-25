package com.example.oulearning.training.application.service;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.port.in.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.port.in.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.port.in.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.port.in.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.oulearning.training.domain.repository.TrainingRepository;
import com.example.oulearning.training.domain.model.TrainingTestFactory;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetTrainingServiceTest {

    private final TrainingRepository repository = mock(TrainingRepository.class);
    private final GetTrainingService service = new GetTrainingService(repository);

    @Test
    @DisplayName("given existing training id, when getting training, then training is returned")
    void givenExistingTrainingId_whenGettingTraining_thenTrainingIsReturned() {
        // given
        final var training = TrainingTestFactory.randomTraining();
        when(repository.findById(training.id())).thenReturn(Optional.of(training));

        // when
        final var result = service.execute(training.id());

        // then
        assertThat(result).isEqualTo(training);
    }

    @Test
    @DisplayName("given non-existing training id, when getting training, then throw TrainingNotFoundException")
    void givenNonExistingTrainingId_whenGettingTraining_thenThrowTrainingNotFoundException() {
        // given
        final var id = TrainingTestFactory.randomTrainingId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(id))
                .isInstanceOf(TrainingNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
