package com.example.oulearning.organization.application.hierarchy.port.in;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.util.Set;

public record AssignMemberCommand(OrganizationalUnitId ouId, Set<EmployeeId> employeeIds) {
}
