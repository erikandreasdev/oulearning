package com.example.oulearning.training.domain;

import java.util.Optional;


public record TrainingPurpose(TrainingPurposeType type, String otherPurpose) {

    public TrainingPurpose {
        TrainingGuard.requirePurposeType(type);
        if (type == TrainingPurposeType.OTHER) {
            otherPurpose = TrainingGuard.requireValidOtherPurposeDescription(otherPurpose);
        } else {
            otherPurpose = null;
        }
    }

    public static TrainingPurpose individualDevelopmentPlan() {
        return new TrainingPurpose(TrainingPurposeType.INDIVIDUAL_DEVELOPMENT_PLAN, null);
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
