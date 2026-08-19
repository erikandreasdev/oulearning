package com.example.oulearning.organization.application.port.in.command;

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
        UUID parentId,
        Set<UUID> childIds) {

    public CreateOrganizationalUnitCommand {
        ownerCorporateKeys = ownerCorporateKeys != null ? Set.copyOf(ownerCorporateKeys) : Set.of();
        childIds = childIds != null ? Set.copyOf(childIds) : Set.of();
    }
}
