package com.example.oulearning.organization.application.employee.port.in;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;

public record UpdateEmployeeCommand(EmployeeId id, String name, String surname, String email) {
}
