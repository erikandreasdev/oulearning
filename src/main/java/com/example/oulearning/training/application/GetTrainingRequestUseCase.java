package com.example.oulearning.training.application;

import com.example.oulearning.training.domain.TrainingRequest;
import java.util.Optional;

/**
 * Use case interface for retrieving a single training request.
 */
public interface GetTrainingRequestUseCase {

    Optional<TrainingRequest> execute(GetTrainingRequestQuery query);
}
