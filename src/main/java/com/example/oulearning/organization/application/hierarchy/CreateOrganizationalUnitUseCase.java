package com.example.oulearning.organization.application.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;

public interface CreateOrganizationalUnitUseCase {
    OrganizationalUnitId execute(CreateOrganizationalUnitCommand command);
}
