package com.example.oulearning.organization.domain.employee.event;

import com.example.oulearning.organization.domain.employee.Email;
import com.example.oulearning.organization.domain.employee.FullName;
import com.example.oulearning.organization.domain.employee.Id;
import java.time.Instant;
import java.util.Objects;

/**
 * Domain event emitted when a new employee is created.
 */
public record EmployeeCreated(Id employeeId, FullName fullName, Email email, Instant occurredAt) {

    public EmployeeCreated {
        Objects.requireNonNull(employeeId, "employeeId cannot be null");
        Objects.requireNonNull(fullName, "fullName cannot be null");
        Objects.requireNonNull(email, "email cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
    }
}
