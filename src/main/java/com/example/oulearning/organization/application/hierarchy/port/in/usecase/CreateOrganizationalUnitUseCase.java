package com.example.oulearning.organization.application.hierarchy.port.in.usecase;

import com.example.oulearning.organization.application.hierarchy.port.in.command.CreateOrganizationalUnitCommand;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;

public interface CreateOrganizationalUnitUseCase {
    OrganizationalUnitId execute(CreateOrganizationalUnitCommand command);
}
