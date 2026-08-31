package com.example.oulearning.organization.application.hierarchy.port.in.model;

import com.example.oulearning.organization.domain.employee.model.Employee;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import java.util.List;

public record ImportOrganizationResult(
        int employeesProcessed,
        int organizationalUnitsProcessed,
        int ownersAssigned,
        int membersAssigned,
        List<Employee> employees,
        List<OrganizationalUnit> organizationalUnits) {

    public ImportOrganizationResult {
        employees = employees == null ? List.of() : List.copyOf(employees);
        organizationalUnits = organizationalUnits == null ? List.of() : List.copyOf(organizationalUnits);
    }
}
