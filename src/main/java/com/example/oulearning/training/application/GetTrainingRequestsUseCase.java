package com.example.oulearning.training.application;

import com.example.oulearning.training.domain.TrainingRequest;
import java.util.List;

/**
 * Use case interface for searching and querying training requests.
 */
public interface GetTrainingRequestsUseCase {

    List<TrainingRequest> execute(GetTrainingRequestsQuery query);
}
