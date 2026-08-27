package com.example.oulearning.organization.application.hierarchy.port.in.usecase;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import java.util.List;

public interface GetSubtreeOrganizationalUnitsUseCase {
    List<OrganizationalUnit> execute(OrganizationalUnitId id);
}
