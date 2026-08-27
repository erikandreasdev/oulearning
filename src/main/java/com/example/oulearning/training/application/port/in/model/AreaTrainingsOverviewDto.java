package com.example.oulearning.training.application.port.in.model;

import java.math.BigDecimal;
import java.util.List;

public record AreaTrainingsOverviewDto(
        BigDecimal assignedBudget,
        BigDecimal availableBudget,
        List<AreaTrainingItemDto> trainings) {}
