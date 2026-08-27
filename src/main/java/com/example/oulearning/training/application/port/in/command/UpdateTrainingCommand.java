package com.example.oulearning.training.application.port.in;

import com.example.oulearning.training.domain.model.TrainingId;
import com.example.oulearning.training.domain.model.TrainingPurposeType;
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
