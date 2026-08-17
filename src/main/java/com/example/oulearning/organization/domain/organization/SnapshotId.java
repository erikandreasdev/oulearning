package com.example.oulearning.organization.domain.organization;

import com.example.oulearning.organization.domain.organization.exception.InvalidOrganizationException;
import java.util.UUID;

/**
 * Strongly-typed identity value object for an organization snapshot.
 */
public record SnapshotId(UUID value) {

    public SnapshotId {
        if (value == null) {
            throw new InvalidOrganizationException("SnapshotId cannot be null");
        }
    }

    public static SnapshotId of(UUID value) {
        return new SnapshotId(value);
    }

    public static SnapshotId of(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidOrganizationException("SnapshotId string representation cannot be null or blank");
        }
        try {
            return new SnapshotId(UUID.fromString(rawValue.strip()));
        } catch (IllegalArgumentException e) {
            throw new InvalidOrganizationException(
                    "Invalid UUID format for SnapshotId: '%s'".formatted(rawValue));
        }
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
