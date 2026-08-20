package com.example.oulearning.training.domain;

import java.util.UUID;


public record TypeId(UUID value) {

    public TypeId {
        TrainingGuard.requireTypeId(value);
    }

    public static TypeId of(final UUID value) {
        return new TypeId(value);
    }

    public static TypeId fromString(final String value) {
        return new TypeId(TrainingGuard.requireValidTypeId(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
