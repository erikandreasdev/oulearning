package com.example.oulearning.training.domain;


public record TypeId(long value) {

    public TypeId {
        TrainingGuard.requirePositiveTypeId(value);
    }

    public static TypeId of(final long value) {
        return new TypeId(value);
    }

    public static TypeId fromString(final String value) {
        return new TypeId(TrainingGuard.requireValidTypeId(value));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
