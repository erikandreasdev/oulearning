package com.example.oulearning.budgeting.application.port.in.command;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.math.BigDecimal;

public record CreateBudgetCommand(
        OrganizationalUnitId ouId, int fiscalYear, BigDecimal total, BigDecimal reserved, BigDecimal available) {
}
