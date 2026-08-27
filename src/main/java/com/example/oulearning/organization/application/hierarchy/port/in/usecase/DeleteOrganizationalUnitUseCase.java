package com.example.oulearning.organization.application.hierarchy.port.in;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;

public interface DeleteOrganizationalUnitUseCase {
    void execute(OrganizationalUnitId id);
}
