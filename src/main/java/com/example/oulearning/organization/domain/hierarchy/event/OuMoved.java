package com.example.oulearning.organization.domain.hierarchy.event;

import com.example.oulearning.organization.domain.hierarchy.Id;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Domain event emitted when an organizational unit is moved to a new parent.
 */
public record OuMoved(Id ouId, Id oldParentId, Id newParentId, Instant occurredAt) {

    public OuMoved {
        Objects.requireNonNull(ouId, "ouId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    public Optional<Id> optionalOldParentId() {
        return Optional.ofNullable(oldParentId);
    }

    public Optional<Id> optionalNewParentId() {
        return Optional.ofNullable(newParentId);
    }
}
