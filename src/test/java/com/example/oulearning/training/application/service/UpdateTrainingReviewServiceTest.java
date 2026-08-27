package com.example.oulearning.training.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.training.application.port.in.command.UpdateTrainingReviewCommand;
import com.example.oulearning.training.domain.model.Modality;
import com.example.oulearning.training.domain.model.Training;
import com.example.oulearning.training.domain.model.TrainingTestFactory;
import com.example.oulearning.training.domain.repository.TrainingRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UpdateTrainingReviewServiceTest {

    private final TrainingRepository trainingRepository = mock(TrainingRepository.class);
    private final Clock clock = Clock.fixed(Instant.parse("2026-08-27T10:00:00Z"), ZoneOffset.UTC);
    private final UpdateTrainingReviewService service = new UpdateTrainingReviewService(trainingRepository, clock);

    @Test
    @DisplayName("given update review command, when executing, then update review and save training")
    void givenUpdateReviewCommand_whenExecuting_thenUpdateReviewAndSaveTraining() {
        // given
        final var training = TrainingTestFactory.randomTraining();
        when(trainingRepository.findById(training.id())).thenReturn(Optional.of(training));

        final var command = new UpdateTrainingReviewCommand(
                training.id(),
                "Approved after review",
                Modality.VIRTUAL,
                clock.instant().plusSeconds(3600),
                clock.instant().plusSeconds(7200),
                null,
                clock.instant());

        // when
        final var result = service.execute(command);

        // then
        assertThat(result.managerReview()).isPresent();
        assertThat(result.managerReview().get().comments()).isEqualTo("Approved after review");
        final var captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingRepository).save(captor.capture());
    }
}
