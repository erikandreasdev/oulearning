package com.example.oulearning.organization.domain;

import java.util.UUID;

/**
 * Strongly-typed identity value object for an organization snapshot.
 *
 * @param value the underlying {@link UUID}
 */
public record SnapshotId(UUID value) {

    /**
     * Compact constructor enforcing non-null value.
     */
    public SnapshotId {
        if (value == null) {
            throw new InvalidOrganizationException("SnapshotId cannot be null");
        }
    }

    /**
     * Factory method creating a {@link SnapshotId} from a {@link UUID}.
     *
     * @param value the UUID
     * @return the {@link SnapshotId}
     */
    public static SnapshotId of(UUID value) {
        return new SnapshotId(value);
    }

    /**
     * Factory method creating a {@link SnapshotId} from a string representation.
     *
     * @param rawValue the UUID string
     * @return the {@link SnapshotId}
     */
    public static SnapshotId of(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidOrganizationException("SnapshotId string representation cannot be null or blank");
        }
        try {
            return new SnapshotId(UUID.fromString(rawValue.strip()));
        } catch (IllegalArgumentException e) {
            throw new InvalidOrganizationException("Invalid UUID format for SnapshotId: '%s'".formatted(rawValue));
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
