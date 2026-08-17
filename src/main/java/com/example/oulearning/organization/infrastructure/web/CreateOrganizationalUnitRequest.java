package com.example.oulearning.organization.infrastructure.web;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.Set;
import java.util.UUID;

/**
 * REST Request DTO for creating an Organizational Unit.
 */
@Schema(description = "Payload for creating a leaf or composite organizational unit")
public record CreateOrganizationalUnitRequest(
        @Schema(description = "Optional custom UUID (auto-generated if omitted)", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID id,

        @NotBlank(message = "OU name cannot be blank")
        @Schema(description = "Unique name of the organizational unit", example = "Core Engineering")
        String name,

        @Schema(description = "Organizational unit type (ORGANIZATION, AREA, SUBAREA)", example = "AREA")
        String ouType,

        @Schema(description = "Corporate keys of designated unit owners", example = "[\"CK0001\", \"CK0002\"]")
        Set<String> ownerCorporateKeys,

        @Schema(description = "Parent OU UUIDs", example = "[]")
        Set<UUID> parentIds,

        @Schema(description = "Child OU UUIDs", example = "[\"b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22\"]")
        Set<UUID> childIds) {

    public CreateOrganizationalUnitRequest {
        ownerCorporateKeys = ownerCorporateKeys != null ? Set.copyOf(ownerCorporateKeys) : Set.of();
        parentIds = parentIds != null ? Set.copyOf(parentIds) : Set.of();
        childIds = childIds != null ? Set.copyOf(childIds) : Set.of();
    }
}
