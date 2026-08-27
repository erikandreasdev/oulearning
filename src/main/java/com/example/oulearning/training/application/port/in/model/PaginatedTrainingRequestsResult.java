package com.example.oulearning.training.application.port.in.model;

import com.example.oulearning.training.domain.model.Training;
import java.util.List;

public record PaginatedTrainingRequestsResult(
        List<Training> items,
        long totalElements,
        int totalPages,
        int page,
        int size) {}
