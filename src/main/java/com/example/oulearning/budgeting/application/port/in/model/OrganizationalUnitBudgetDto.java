package com.example.oulearning.budgeting.application.port.in.model;

import com.example.oulearning.budgeting.domain.model.BudgetId;
import com.example.oulearning.budgeting.domain.model.FiscalYear;
import com.example.oulearning.budgeting.domain.model.Money;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.util.List;

public record OrganizationalUnitBudgetDto(
        BudgetId id,
        OrganizationalUnitId organizationalUnitId,
        Money total,
        Money available,
        Money reserved,
        FiscalYear fiscalYear,
        List<EmployeeId> owners) {}
