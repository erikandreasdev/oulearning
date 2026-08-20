package com.example.oulearning.organization.domain.hierarchy;

public record OrganizationalUnitId(long value) {

    public OrganizationalUnitId {
        HierarchyGuard.requireOrganizationalUnitId(value);
    }

    public static OrganizationalUnitId of(final long value) {
        return new OrganizationalUnitId(value);
    }

    public static OrganizationalUnitId fromString(final String value) {
        return new OrganizationalUnitId(HierarchyGuard.requireValidOrganizationalUnitId(value));
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
