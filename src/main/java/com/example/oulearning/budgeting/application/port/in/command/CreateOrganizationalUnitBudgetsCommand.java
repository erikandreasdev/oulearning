package com.example.oulearning.budgeting.application.port.in.command;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.math.BigDecimal;
import java.util.Set;

public record CreateOrganizationalUnitBudgetsCommand(
        BigDecimal assignedBudget,
        Integer fiscalYear,
        OrganizationalUnitId organizationalUnitId,
        Boolean includeAllChildren,
        Set<OrganizationalUnitId> targetChildOuIds,
        Integer page,
        Integer size) {}
