package com.example.oulearning.organization.domain.employee;

import java.util.UUID;

/**
 * Value object representing an employee identifier.
 *
 * @param value the UUID value
 */
public record EmployeeId(UUID value) {

    public EmployeeId {
        EmployeeGuard.requireNonNull(value, "Employee id");
    }

    public static EmployeeId of(final UUID value) {
        return new EmployeeId(value);
    }

    public static EmployeeId fromString(final String value) {
        return new EmployeeId(EmployeeGuard.requireValidUuid(value, "Employee id"));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
