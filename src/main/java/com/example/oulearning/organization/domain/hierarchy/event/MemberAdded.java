package com.example.oulearning.organization.domain.hierarchy.event;

import com.example.oulearning.organization.domain.hierarchy.Id;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a member is added to an organizational unit.
 */
public record MemberAdded(
        Id ouId,
        com.example.oulearning.organization.domain.employee.Id employeeId,
        Instant occurredAt) {

    public MemberAdded {
        Objects.requireNonNull(ouId, "ouId cannot be null");
        Objects.requireNonNull(employeeId, "employeeId cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}
