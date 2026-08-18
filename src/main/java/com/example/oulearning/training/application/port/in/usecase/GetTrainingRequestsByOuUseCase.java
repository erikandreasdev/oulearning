package com.example.oulearning.training.application.port.in.usecase;

import com.example.oulearning.training.domain.request.TrainingRequest;
import java.util.List;
import com.example.oulearning.training.application.port.in.query.GetTrainingRequestsByOuQuery;

/**
 * Use case interface for retrieving training requests by OU and optional Fiscal Year.
 */
public interface GetTrainingRequestsByOuUseCase {

    List<TrainingRequest> execute(GetTrainingRequestsByOuQuery query);
}
