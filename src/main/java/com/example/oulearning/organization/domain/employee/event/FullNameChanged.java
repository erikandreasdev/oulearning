package com.example.oulearning.organization.domain.employee.event;

import com.example.oulearning.organization.domain.employee.FullName;
import com.example.oulearning.organization.domain.employee.Id;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when an employee's full name is changed.
 */
public record FullNameChanged(Id employeeId, FullName oldFullName, FullName newFullName, Instant occurredAt) {

    public FullNameChanged {
        Objects.requireNonNull(employeeId, "employeeId cannot be null");
        Objects.requireNonNull(oldFullName, "oldFullName cannot be null");
        Objects.requireNonNull(newFullName, "newFullName cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}
