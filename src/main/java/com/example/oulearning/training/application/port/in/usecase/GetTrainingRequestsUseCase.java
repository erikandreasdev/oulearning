package com.example.oulearning.training.application.port.in.usecase;

import com.example.oulearning.training.domain.request.TrainingRequest;
import java.util.List;
import com.example.oulearning.training.application.port.in.query.GetTrainingRequestsQuery;

/**
 * Use case interface for searching and querying training requests.
 */
public interface GetTrainingRequestsUseCase {

    List<TrainingRequest> execute(GetTrainingRequestsQuery query);
}
