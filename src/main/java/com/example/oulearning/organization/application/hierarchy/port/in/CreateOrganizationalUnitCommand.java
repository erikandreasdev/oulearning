package com.example.oulearning.organization.application.hierarchy.port.in;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;

public record CreateOrganizationalUnitCommand(String name, OrganizationalUnitId parentId) {
}
