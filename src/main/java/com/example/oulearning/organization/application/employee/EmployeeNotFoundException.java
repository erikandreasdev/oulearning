package com.example.oulearning.organization.application.employee;

import com.example.oulearning.organization.domain.employee.EmployeeId;

public class EmployeeNotFoundException extends RuntimeException {

    private final EmployeeId employeeId;

    public EmployeeNotFoundException(final EmployeeId employeeId) {
        super("Employee not found with id: %s".formatted(employeeId));
        this.employeeId = employeeId;
    }

    public EmployeeId employeeId() {
        return employeeId;
    }
}
