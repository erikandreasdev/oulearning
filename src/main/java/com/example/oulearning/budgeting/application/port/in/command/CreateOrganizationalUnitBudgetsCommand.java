package com.example.oulearning.budgeting.application.port.in.command;

import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.math.BigDecimal;
import java.util.Set;

public record CreateOrganizationalUnitBudgetsCommand(
        BigDecimal assignedBudget,
        Integer fiscalYear,
        OrganizationalUnitId organizationalUnitId,
        Set<EmployeeId> owners,
        Boolean includeAllChildren,
        Set<OrganizationalUnitId> targetChildOuIds,
        Integer page,
        Integer size) {

    public CreateOrganizationalUnitBudgetsCommand {
        if (owners == null || owners.isEmpty()) {
            throw new IllegalArgumentException("At least one owner must be assigned");
        }
        owners = Set.copyOf(owners);
        targetChildOuIds = (targetChildOuIds != null) ? Set.copyOf(targetChildOuIds) : Set.of();
    }
}
