package com.example.oulearning.organization.domain.hierarchy.event;

import com.example.oulearning.organization.domain.hierarchy.Id;
import com.example.oulearning.organization.domain.hierarchy.Name;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Domain event emitted when an organizational unit is created.
 */
public record OuCreated(Id ouId, Name name, Id parentId, Instant occurredAt) {

    public OuCreated {
        Objects.requireNonNull(ouId, "ouId cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }

    public Optional<Id> optionalParentId() {
        return Optional.ofNullable(parentId);
    }
}
