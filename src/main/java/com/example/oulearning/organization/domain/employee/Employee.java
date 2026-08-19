package com.example.oulearning.organization.domain.employee;

import com.example.oulearning.organization.domain.employee.event.EmailChanged;
import com.example.oulearning.organization.domain.employee.event.EmployeeCreated;
import com.example.oulearning.organization.domain.employee.event.FullNameChanged;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Aggregate root representing an employee.
 */
public final class Employee {

    private final Id id;
    private FullName fullName;
    private Email email;
    private final List<Object> domainEvents = new ArrayList<>();

    private Employee(Id id, FullName fullName, Email email) {
        this.id = Objects.requireNonNull(id, "Employee id cannot be null");
        this.fullName = Objects.requireNonNull(fullName, "FullName cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
    }

    /**
     * Factory method to create a new {@link Employee} and register an {@link EmployeeCreated} event.
     */
    public static Employee create(Id id, FullName fullName, Email email, Instant createdAt) {
        Employee employee = new Employee(id, fullName, email);
        employee.registerEvent(new EmployeeCreated(id, fullName, email, Objects.requireNonNull(createdAt, "createdAt cannot be null")));
        return employee;
    }

    /**
     * Factory method to reconstitute an existing {@link Employee} without registering domain events.
     */
    public static Employee reconstitute(Id id, FullName fullName, Email email) {
        return new Employee(id, fullName, email);
    }

    public void changeFullName(FullName newFullName, Instant occurredAt) {
        Objects.requireNonNull(newFullName, "newFullName cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (!this.fullName.equals(newFullName)) {
            FullName oldFullName = this.fullName;
            this.fullName = newFullName;
            registerEvent(new FullNameChanged(this.id, oldFullName, newFullName, occurredAt));
        }
    }

    public void changeEmail(Email newEmail, Instant occurredAt) {
        Objects.requireNonNull(newEmail, "newEmail cannot be null");
        Objects.requireNonNull(occurredAt, "occurredAt cannot be null");
        if (!this.email.equals(newEmail)) {
            Email oldEmail = this.email;
            this.email = newEmail;
            registerEvent(new EmailChanged(this.id, oldEmail, newEmail, occurredAt));
        }
    }

    public Id id() {
        return id;
    }

    public FullName fullName() {
        return fullName;
    }

    public Email email() {
        return email;
    }

    private void registerEvent(Object event) {
        this.domainEvents.add(event);
    }

    public List<Object> pullDomainEvents() {
        List<Object> events = List.copyOf(domainEvents);
        domainEvents.clear();
        return Collections.unmodifiableList(events);
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
