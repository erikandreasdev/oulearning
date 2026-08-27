package com.example.oulearning.training.application.port.in.command;

import com.example.oulearning.training.domain.model.ExternalProviderId;
import com.example.oulearning.training.domain.model.Modality;
import com.example.oulearning.training.domain.model.TrainingId;
import java.time.Instant;

public record UpdateTrainingReviewCommand(
        TrainingId trainingId,
        String comments,
        Modality modality,
        Instant startDate,
        Instant endDate,
        ExternalProviderId externalProviderId,
        Instant reviewedAt) {}
