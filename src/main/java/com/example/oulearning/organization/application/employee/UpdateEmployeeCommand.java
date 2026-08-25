package com.example.oulearning.organization.application.employee;

import com.example.oulearning.organization.domain.employee.EmployeeId;

public record UpdateEmployeeCommand(EmployeeId id, String name, String surname, String email) {
}
