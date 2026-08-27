package com.example.oulearning.training.application.port.in.usecase;

import com.example.oulearning.training.application.port.in.command.ListTrainingRequestsQuery;
import com.example.oulearning.training.application.port.in.model.PaginatedTrainingRequestsResult;

public interface ListTrainingRequestsUseCase {
    PaginatedTrainingRequestsResult execute(ListTrainingRequestsQuery query);
}
