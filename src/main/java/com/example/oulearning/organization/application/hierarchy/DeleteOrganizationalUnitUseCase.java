package com.example.oulearning.organization.application.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;

public interface DeleteOrganizationalUnitUseCase {
    void execute(OrganizationalUnitId id);
}
