package com.example.oulearning.organization.domain.hierarchy;


public record Name(String value) {

    public Name {
        value = HierarchyGuard.requireValidOrganizationalUnitName(value);
    }

    public static Name of(final String value) {
        return new Name(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
