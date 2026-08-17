package com.example.oulearning.training.application;

import com.example.oulearning.training.domain.TrainingRequest;
import java.util.List;

/**
 * Use case interface for retrieving training requests by OU and optional Fiscal Year.
 */
public interface GetTrainingRequestsByOuUseCase {

    List<TrainingRequest> execute(GetTrainingRequestsByOuQuery query);
}
