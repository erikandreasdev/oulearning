package com.example.oulearning.training.domain;

import java.util.Optional;


public record TrainingPurpose(TrainingPurposeType type, String otherPurpose) {

    public TrainingPurpose {
        TrainingGuard.requireNonNull(type, "TrainingPurposeType");
        if (type == TrainingPurposeType.OTHER) {
            otherPurpose = TrainingGuard.requireLengthBetween(
                    otherPurpose,
                    "Purpose description",
                    TrainingConstants.MIN_PURPOSE_LENGTH,
                    TrainingConstants.MAX_PURPOSE_LENGTH);
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

    public static TrainingPurpose other(final String purpose) {
        return new TrainingPurpose(TrainingPurposeType.OTHER, purpose);
    }

    public Optional<String> optionalOtherPurpose() {
        return Optional.ofNullable(otherPurpose);
    }
}
