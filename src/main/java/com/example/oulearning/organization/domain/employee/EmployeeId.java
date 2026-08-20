package com.example.oulearning.organization.domain.employee;

import java.util.UUID;


public record EmployeeId(UUID value) {

    public EmployeeId {
        EmployeeGuard.requireEmployeeId(value);
    }

    public static EmployeeId of(final UUID value) {
        return new EmployeeId(value);
    }

    public static EmployeeId fromString(final String value) {
        return new EmployeeId(EmployeeGuard.requireValidEmployeeId(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
