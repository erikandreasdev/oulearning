package com.example.oulearning.budgeting.application;

import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import java.math.BigDecimal;

public record CreateBudgetCommand(
        OrganizationalUnitId ouId, int fiscalYear, BigDecimal total, BigDecimal reserved, BigDecimal available) {
}
