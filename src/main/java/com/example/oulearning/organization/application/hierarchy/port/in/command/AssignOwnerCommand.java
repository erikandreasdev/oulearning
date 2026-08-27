package com.example.oulearning.organization.application.hierarchy.port.in.command;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.util.Set;

public record AssignOwnerCommand(OrganizationalUnitId ouId, Set<EmployeeId> employeeIds) {
}
