package com.example.oulearning.training.domain;

import com.example.oulearning.training.domain.exception.InvalidTrainingRequestException;
import java.util.Objects;
import java.util.Optional;

/**
 * Value Object representing the purpose of a training request.
 * If type is OTHER, a non-empty customText description is required.
 */
public record TrainingPurpose(TrainingPurposeType type, String customText) {

    public TrainingPurpose {
        Objects.requireNonNull(type, "TrainingPurposeType cannot be null");
        if (type == TrainingPurposeType.OTHER) {
            if (customText == null || customText.isBlank()) {
                throw new InvalidTrainingRequestException("Custom purpose text is required when purpose type is OTHER");
            }
            customText = customText.trim();
        } else {
            customText = null;
        }
    }

    public static TrainingPurpose of(TrainingPurposeType type) {
        return new TrainingPurpose(type, null);
    }

    public static TrainingPurpose other(String customText) {
        return new TrainingPurpose(TrainingPurposeType.OTHER, customText);
    }

    public Optional<String> customTextOptional() {
        return Optional.ofNullable(customText);
    }
}
