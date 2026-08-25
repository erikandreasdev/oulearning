package com.example.oulearning.training.application;

import com.example.oulearning.training.domain.TrainingId;
import com.example.oulearning.training.domain.TrainingPurposeType;
import java.math.BigDecimal;

public record UpdateTrainingCommand(
        TrainingId id,
        String name,
        BigDecimal costAmount,
        String currency,
        int hours,
        TrainingPurposeType purposeType,
        String purposeDescription,
        long typeId) {
}
