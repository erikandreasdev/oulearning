package com.example.oulearning.organization.application;

import java.util.Set;
import java.util.UUID;

/**
 * Immutable application command for creating an Organizational Unit.
 */
public record CreateOrganizationalUnitCommand(
        UUID id,
        String name,
        String ouType,
        Set<String> ownerCorporateKeys,
        Set<UUID> parentIds,
        Set<UUID> childIds) {

    public CreateOrganizationalUnitCommand {
        ownerCorporateKeys = ownerCorporateKeys != null ? Set.copyOf(ownerCorporateKeys) : Set.of();
        parentIds = parentIds != null ? Set.copyOf(parentIds) : Set.of();
        childIds = childIds != null ? Set.copyOf(childIds) : Set.of();
    }
}
