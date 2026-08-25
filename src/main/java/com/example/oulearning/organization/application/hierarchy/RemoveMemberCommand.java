package com.example.oulearning.organization.application.hierarchy;

import com.example.oulearning.organization.domain.employee.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import java.util.Set;

public record RemoveMemberCommand(OrganizationalUnitId ouId, Set<EmployeeId> employeeIds) {
}
