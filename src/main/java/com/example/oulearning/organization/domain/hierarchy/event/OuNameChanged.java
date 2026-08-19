package com.example.oulearning.organization.domain.hierarchy.event;

import com.example.oulearning.organization.domain.hierarchy.Id;
import com.example.oulearning.organization.domain.hierarchy.Name;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when an organizational unit's name is changed.
 */
public record OuNameChanged(Id ouId, Name oldName, Name newName, Instant occurredAt) {

    public OuNameChanged {
        Objects.requireNonNull(ouId, "ouId cannot be null");
        Objects.requireNonNull(oldName, "oldName cannot be null");
        Objects.requireNonNull(newName, "newName cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}
