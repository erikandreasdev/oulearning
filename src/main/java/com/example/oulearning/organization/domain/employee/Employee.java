package com.example.oulearning.organization.domain.employee;

import java.util.Objects;

/**
 * Domain object representing an Employee.
 */
public final class Employee {

    private final EmployeeId id;
    private final FullName fullName;
    private final Email email;

    public Employee(EmployeeId id, FullName fullName, Email email) {
        this.id = Objects.requireNonNull(id, "Employee id cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "FullName cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
    }

    public static Employee of(EmployeeId id, FullName fullName, Email email) {
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
    public boolean equals(Object o) {
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
        return "Employee[id=" + id + ", fullName=" + fullName + ", email=" + email + "]";
    }
}
