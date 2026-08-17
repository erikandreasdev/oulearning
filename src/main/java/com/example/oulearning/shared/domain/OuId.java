package com.example.oulearning.shared.domain;

import java.util.UUID;

/**
 * Strongly-typed identity value object for an organizational unit.
 *
 * @param value the underlying {@link UUID}
 */
public record OuId(UUID value) {

    /**
     * Compact constructor enforcing non-null ID invariant.
     */
    public OuId {
        if (value == null) {
            throw new InvalidOuIdException("OuId cannot be null");
        }
    }

    /**
     * Factory method to create an {@link OuId} from a {@link UUID}.
     *
     * @param value the UUID
     * @return the {@link OuId}
     */
    public static OuId of(UUID value) {
        return new OuId(value);
    }

    /**
     * Factory method to create an {@link OuId} from a string representation of a UUID.
     *
     * @param rawValue the UUID string
     * @return the {@link OuId}
     */
    public static OuId of(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidOuIdException("OuId string representation cannot be null or blank");
        }
        try {
            return new OuId(UUID.fromString(rawValue.strip()));
        } catch (IllegalArgumentException e) {
            throw new InvalidOuIdException("Invalid UUID format for OuId: '%s'".formatted(rawValue));
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
