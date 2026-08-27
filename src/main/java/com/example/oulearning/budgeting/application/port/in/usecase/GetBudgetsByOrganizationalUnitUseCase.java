package com.example.oulearning.budgeting.application.port.in.usecase;

import com.example.oulearning.budgeting.application.port.in.model.OrganizationalUnitBudgetDto;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.util.List;

public interface GetBudgetsByOrganizationalUnitUseCase {
    List<OrganizationalUnitBudgetDto> execute(OrganizationalUnitId organizationalUnitId, boolean includeSubtree);
}
