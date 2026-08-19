package com.example.oulearning.training.domain;

import java.util.UUID;

/**
 * Value object representing a training type identifier.
 *
 * @param value the UUID value
 */
public record TypeId(UUID value) {

    public TypeId {
        if (value == null) {
            throw new InvalidTrainingOperationException("TypeId cannot be null");
        }
    }

    public static TypeId of(UUID value) {
        return new TypeId(value);
    }

    public static TypeId fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidTrainingOperationException("TypeId string cannot be null or blank");
        }
        try {
            return new TypeId(UUID.fromString(value.strip()));
        } catch (IllegalArgumentException e) {
            throw new InvalidTrainingOperationException("Invalid UUID format: " + value);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
