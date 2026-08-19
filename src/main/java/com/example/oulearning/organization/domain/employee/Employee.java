package com.example.oulearning.organization.domain.employee;

import java.util.Objects;


public final class Employee {

    private final EmployeeId id;
    private final FullName fullName;
    private final Email email;

    public Employee(final EmployeeId id, final FullName fullName, final Email email) {
        this.id = EmployeeGuard.requireNonNull(id, "Employee id");
        this.fullName = EmployeeGuard.requireNonNull(fullName, "FullName");
        this.email = EmployeeGuard.requireNonNull(email, "Email");
    }

    public static Employee of(final EmployeeId id, final FullName fullName, final Email email) {
        return new Employee(id, fullName, email);
    }

    public EmployeeId id() {
        return id;
    }

    public FullName fullName() {
        return fullName;
    }

    public Email email() {
        return email;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee employee)) return false;
        return Objects.equals(id, employee.id);
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
