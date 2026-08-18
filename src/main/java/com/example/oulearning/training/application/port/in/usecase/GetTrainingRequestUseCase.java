package com.example.oulearning.training.application.port.in.usecase;

import com.example.oulearning.training.domain.request.TrainingRequest;
import java.util.Optional;
import com.example.oulearning.training.application.port.in.query.GetTrainingRequestQuery;

/**
 * Use case interface for retrieving a single training request.
 */
public interface GetTrainingRequestUseCase {

    Optional<TrainingRequest> execute(GetTrainingRequestQuery query);
}
