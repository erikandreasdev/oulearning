package com.example.oulearning.organization.application.hierarchy.port.in.usecase;

import com.example.oulearning.organization.application.hierarchy.port.in.command.UpdateOrganizationalUnitCommand;

public interface UpdateOrganizationalUnitUseCase {
    void execute(UpdateOrganizationalUnitCommand command);
}
