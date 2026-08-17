package com.example.oulearning.organization.application;

import java.util.UUID;

/**
 * Immutable application query for retrieving an Organizational Unit.
 */
public record GetOrganizationalUnitQuery(
        UUID id,
        String name,
        boolean includeSubtree) {

    public static GetOrganizationalUnitQuery byId(UUID id, boolean includeSubtree) {
        return new GetOrganizationalUnitQuery(id, null, includeSubtree);
    }

    public static GetOrganizationalUnitQuery byName(String name) {
        return new GetOrganizationalUnitQuery(null, name, false);
    }
}
