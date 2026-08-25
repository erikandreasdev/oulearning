package com.example.oulearning.organization.domain.employee;

import java.util.Objects;

public record Employee(EmployeeId id, FullName fullName, Email email, boolean active) {

    public Employee {
        EmployeeGuard.requireEmployeeId(id);
        EmployeeGuard.requireFullName(fullName);
        EmployeeGuard.requireEmail(email);
    }

    public static Employee create(final EmployeeId id, final FullName fullName, final Email email) {
        return new Employee(id, fullName, email, true);
    }

    public static Employee reconstitute(
            final EmployeeId id, final FullName fullName, final Email email, final boolean active) {
        return new Employee(id, fullName, email, active);
    }

    public static Employee of(final EmployeeId id, final FullName fullName, final Email email) {
        return create(id, fullName, email);
    }

    public Employee updateFullName(final FullName newFullName) {
        EmployeeGuard.requireFullName(newFullName);
        return new Employee(id, newFullName, email, active);
    }

    public Employee updateEmail(final Email newEmail) {
        EmployeeGuard.requireEmail(newEmail);
        return new Employee(id, fullName, newEmail, active);
    }

    public Employee deactivate() {
        return new Employee(id, fullName, email, false);
    }

    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof final Employee employee && Objects.equals(id, employee.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
