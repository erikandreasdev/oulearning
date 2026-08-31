package com.example.oulearning.organization.infrastructure.web.dto;

import java.util.List;

public record ImportOrganizationResponseDto(
        int employeesProcessed,
        int organizationalUnitsProcessed,
        int ownersAssigned,
        int membersAssigned,
        List<EmployeeResponseDto> employees,
        List<OrganizationalUnitResponseDto> organizationalUnits) {

    public ImportOrganizationResponseDto {
        employees = employees == null ? List.of() : List.copyOf(employees);
        organizationalUnits = organizationalUnits == null ? List.of() : List.copyOf(organizationalUnits);
    }
}
