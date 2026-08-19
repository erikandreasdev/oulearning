package com.example.oulearning.organization.domain.employee.event;

import com.example.oulearning.organization.domain.employee.Email;
import com.example.oulearning.organization.domain.employee.Id;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when an employee's email address is changed.
 */
public record EmailChanged(Id employeeId, Email oldEmail, Email newEmail, Instant occurredAt) {

    public EmailChanged {
        Objects.requireNonNull(employeeId, "employeeId cannot be null");
        Objects.requireNonNull(oldEmail, "oldEmail cannot be null");
        Objects.requireNonNull(newEmail, "newEmail cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}
