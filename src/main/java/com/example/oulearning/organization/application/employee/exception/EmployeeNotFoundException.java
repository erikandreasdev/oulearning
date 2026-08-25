package com.example.oulearning.organization.application.employee.exception;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;

public class EmployeeNotFoundException extends RuntimeException {

    private final transient EmployeeId employeeId;

    public EmployeeNotFoundException(final EmployeeId employeeId) {
        super("Employee not found with id: %s".formatted(employeeId));
        this.employeeId = employeeId;
    }

    public EmployeeId employeeId() {
        return employeeId;
    }
}
