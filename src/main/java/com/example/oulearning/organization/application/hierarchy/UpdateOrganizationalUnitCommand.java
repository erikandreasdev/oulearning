package com.example.oulearning.organization.application.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;

public record UpdateOrganizationalUnitCommand(OrganizationalUnitId id, String name) {
}
