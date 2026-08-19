package com.example.oulearning.training.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * Value object representing the stated business purpose of a training request.
 *
 * @param type the purpose category
 * @param otherPurpose specific explanation if type is {@link TrainingPurposeType#OTHER}
 */
public record TrainingPurpose(TrainingPurposeType type, String otherPurpose) {

    public TrainingPurpose {
        Objects.requireNonNull(type, "TrainingPurposeType cannot be null");
        if (type == TrainingPurposeType.OTHER) {
            if (otherPurpose == null || otherPurpose.strip().isBlank()) {
                throw new InvalidTrainingOperationException(
                        "Purpose description cannot be blank when type is OTHER");
            }
            otherPurpose = otherPurpose.strip();
        } else {
            otherPurpose = null;
        }
    }

    public static TrainingPurpose idp() {
        return new TrainingPurpose(TrainingPurposeType.IDP, null);
    }

    public static TrainingPurpose departmentGoals() {
        return new TrainingPurpose(TrainingPurposeType.DEPARTMENT_GOALS, null);
    }

    public static TrainingPurpose other(String otherPurpose) {
        return new TrainingPurpose(TrainingPurposeType.OTHER, otherPurpose);
    }

    public Optional<String> optionalOtherPurpose() {
        return Optional.ofNullable(otherPurpose);
    }
}
