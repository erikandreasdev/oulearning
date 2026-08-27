package com.example.oulearning.training.application.service;

import com.example.oulearning.budgeting.domain.model.*;
import com.example.oulearning.budgeting.application.exception.*;
import com.example.oulearning.organization.domain.employee.model.*;
import com.example.oulearning.organization.application.employee.exception.*;
import com.example.oulearning.organization.domain.hierarchy.model.*;
import com.example.oulearning.organization.application.hierarchy.exception.*;
import com.example.oulearning.training.domain.model.*;
import com.example.oulearning.training.application.exception.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import com.example.oulearning.training.domain.model.TrainingTestFactory;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class DeleteTrainingServiceTest {

    private final TrainingRepository repository = mock(TrainingRepository.class);
    private final DeleteTrainingService service = new DeleteTrainingService(repository);

    @Test
    @DisplayName("given existing training, when deleting, then training is deactivated and saved")
    void givenExistingTraining_whenDeleting_thenTrainingIsDeactivatedAndSaved() {
        // given
        final var training = TrainingTestFactory.randomTraining();
        when(repository.findById(training.id())).thenReturn(Optional.of(training));

        // when
        service.execute(training.id());

        // then
        final var captor = ArgumentCaptor.forClass(Training.class);
        verify(repository).save(captor.capture());
        final var saved = captor.getValue();
        assertThat(saved.id()).isEqualTo(training.id());
        assertThat(saved.active()).isFalse();
    }

    @Test
    @DisplayName("given non-existing training, when deleting, then throw TrainingNotFoundException")
    void givenNonExistingTraining_whenDeleting_thenThrowTrainingNotFoundException() {
        // given
        final var id = TrainingTestFactory.randomTrainingId();
        when(repository.findById(id)).thenReturn(Optional.empty());

        // when

        // then
        assertThatThrownBy(() -> service.execute(id))
                .isInstanceOf(TrainingNotFoundException.class);
    }
}
