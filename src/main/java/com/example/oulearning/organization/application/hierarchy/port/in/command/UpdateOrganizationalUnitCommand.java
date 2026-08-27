package com.example.oulearning.organization.application.hierarchy.port.in.command;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;

public record UpdateOrganizationalUnitCommand(OrganizationalUnitId id, String name) {
}
