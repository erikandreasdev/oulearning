package com.example.oulearning.organization.domain.hierarchy;

import java.util.HashSet;
import java.util.Set;

public record Organization(Set<OrganizationalUnitId> organizationalUnitIds) {

    public Organization {
        if (organizationalUnitIds == null) {
            organizationalUnitIds = Set.of();
        } else {
            organizationalUnitIds.forEach(HierarchyGuard::requireOrganizationalUnitId);
            organizationalUnitIds = Set.copyOf(organizationalUnitIds);
        }
    }

    public Organization() {
        this(Set.of());
    }

    public Organization addOrganizationalUnit(final OrganizationalUnitId organizationalUnitId) {
        HierarchyGuard.requireOrganizationalUnitId(organizationalUnitId);
        final var updated = new HashSet<>(organizationalUnitIds);
        updated.add(organizationalUnitId);
        return new Organization(updated);
    }

    public Organization removeOrganizationalUnit(final OrganizationalUnitId organizationalUnitId) {
        HierarchyGuard.requireOrganizationalUnitId(organizationalUnitId);
        final var updated = new HashSet<>(organizationalUnitIds);
        updated.remove(organizationalUnitId);
        return new Organization(updated);
    }

    public boolean contains(final OrganizationalUnitId organizationalUnitId) {
        return organizationalUnitIds.contains(organizationalUnitId);
    }

    public int size() {
        return organizationalUnitIds.size();
    }
}
