package com.example.oulearning.organization.domain.hierarchy;

import java.util.UUID;

/**
 * Value object representing an organizational unit identifier.
 *
 * @param value the UUID value
 */
public record Id(UUID value) {

    public Id {
        if (value == null) {
            throw new InvalidOuException("Id cannot be null");
        }
    }

    public static Id of(UUID value) {
        return new Id(value);
    }

    public static Id fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidOuException("Id string cannot be null or blank");
        }
        try {
            return new Id(UUID.fromString(value.strip()));
        } catch (IllegalArgumentException e) {
            throw new InvalidOuException("Invalid UUID format: " + value);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
