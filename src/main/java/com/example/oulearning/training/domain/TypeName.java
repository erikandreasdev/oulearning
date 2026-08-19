package com.example.oulearning.training.domain;


public record TypeName(String value) {

    public TypeName {
        value = TrainingGuard.requireLengthBetween(
                value, "TypeName", TrainingConstants.MIN_NAME_LENGTH, TrainingConstants.MAX_NAME_LENGTH);
    }

    public static TypeName of(final String value) {
        return new TypeName(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
