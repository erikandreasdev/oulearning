package com.example.oulearning.training.domain;

import java.util.UUID;

/**
 * Value object representing a training type identifier.
 *
 * @param value the UUID value
 */
public record TypeId(UUID value) {

    public TypeId {
        TrainingGuard.requireNonNull(value, "TypeId");
    }

    public static TypeId of(final UUID value) {
        return new TypeId(value);
    }

    public static TypeId fromString(final String value) {
        return new TypeId(TrainingGuard.requireValidUuid(value, "TypeId"));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
