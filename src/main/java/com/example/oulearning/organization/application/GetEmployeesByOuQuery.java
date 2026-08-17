package com.example.oulearning.organization.application;

import java.util.Optional;
import java.util.UUID;

/**
 * Query to retrieve all Employees belonging to an Organizational Unit, optionally including all descendant subtrees.
 */
public record GetEmployeesByOuQuery(
        UUID ouId,
        String ouName,
        boolean includeSubtree) {

    public GetEmployeesByOuQuery {
        if (ouId == null && (ouName == null || ouName.isBlank())) {
            throw new IllegalArgumentException("Either ouId or ouName must be provided");
        }
    }

    public Optional<UUID> findOuId() {
        return Optional.ofNullable(ouId);
    }

    public Optional<String> findOuName() {
        return Optional.ofNullable(ouName);
    }

    public static GetEmployeesByOuQuery byId(UUID ouId, boolean includeSubtree) {
        return new GetEmployeesByOuQuery(ouId, null, includeSubtree);
    }

    public static GetEmployeesByOuQuery byName(String ouName, boolean includeSubtree) {
        return new GetEmployeesByOuQuery(null, ouName, includeSubtree);
    }
}
