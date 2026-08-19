package com.example.oulearning.organization.domain.hierarchy;

import java.util.UUID;

/**
 * Value object representing an organizational unit identifier.
 *
 * @param value the UUID value
 */
public record OuId(UUID value) {

    public OuId {
        if (value == null) {
            throw new InvalidOuException("Ou id cannot be null");
        }
    }

    public static OuId of(UUID value) {
        return new OuId(value);
    }

    public static OuId fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidOuException("Ou id string cannot be null or blank");
        }
        try {
            return new OuId(UUID.fromString(value.strip()));
        } catch (IllegalArgumentException e) {
            throw new InvalidOuException("Invalid UUID format: " + value);
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
