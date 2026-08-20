package com.example.oulearning.organization.domain.employee;

import java.util.Objects;

public record Employee(EmployeeId id, FullName fullName, Email email) {

    public Employee {
        EmployeeGuard.requireEmployeeId(id);
        EmployeeGuard.requireFullName(fullName);
        EmployeeGuard.requireEmail(email);
    }

    public static Employee of(final EmployeeId id, final FullName fullName, final Email email) {
        return new Employee(id, fullName, email);
    }

    @Override
    public boolean equals(final Object o) {
        return this == o || (o instanceof final Employee employee && Objects.equals(id, employee.id));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Employee[id=%s, fullName=%s, email=%s]".formatted(id, fullName, email);
    }
}
