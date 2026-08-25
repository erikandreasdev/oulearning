package com.example.oulearning.organization.application.hierarchy;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import java.util.Set;

public record AssignMemberCommand(OrganizationalUnitId ouId, Set<EmployeeId> employeeIds) {
}
