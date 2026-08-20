package com.example.oulearning.organization.domain.hierarchy;

import java.util.UUID;

public record OrganizationalUnitId(UUID value) {

    public OrganizationalUnitId {
        HierarchyGuard.requireOrganizationalUnitId(value);
    }

    public static OrganizationalUnitId of(final UUID value) {
        return new OrganizationalUnitId(value);
    }

    public static OrganizationalUnitId fromString(final String value) {
        return new OrganizationalUnitId(HierarchyGuard.requireValidOrganizationalUnitId(value));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
