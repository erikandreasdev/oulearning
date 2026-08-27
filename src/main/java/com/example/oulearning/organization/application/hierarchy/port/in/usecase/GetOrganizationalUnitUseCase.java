package com.example.oulearning.organization.application.hierarchy.port.in;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;

public interface GetOrganizationalUnitUseCase {
    OrganizationalUnit execute(OrganizationalUnitId id);
}
