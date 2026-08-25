package com.example.oulearning.organization.application.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;

public class OrganizationalUnitNotFoundException extends RuntimeException {

    private final transient OrganizationalUnitId organizationalUnitId;

    public OrganizationalUnitNotFoundException(final OrganizationalUnitId organizationalUnitId) {
        super("Organizational unit not found with id: %s".formatted(organizationalUnitId));
        this.organizationalUnitId = organizationalUnitId;
    }

    public OrganizationalUnitId organizationalUnitId() {
        return organizationalUnitId;
    }
}
