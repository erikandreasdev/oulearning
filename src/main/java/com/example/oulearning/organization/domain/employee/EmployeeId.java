package com.example.oulearning.organization.domain.employee;


public record EmployeeId(long value) {

    public EmployeeId {
        EmployeeGuard.requireEmployeeId(value);
    }

    public static EmployeeId of(final long value) {
        return new EmployeeId(value);
    }

    public static EmployeeId fromString(final String value) {
        return new EmployeeId(EmployeeGuard.requireValidEmployeeId(value));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
