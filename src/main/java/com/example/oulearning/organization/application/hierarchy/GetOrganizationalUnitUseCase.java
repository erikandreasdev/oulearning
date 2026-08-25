package com.example.oulearning.organization.application.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;

public interface GetOrganizationalUnitUseCase {
    OrganizationalUnit execute(OrganizationalUnitId id);
}
