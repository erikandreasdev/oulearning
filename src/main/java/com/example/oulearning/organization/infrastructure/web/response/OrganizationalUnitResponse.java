package com.example.oulearning.organization.infrastructure.web.response;

import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * REST Response DTO representing an Organizational Unit.
 */
@Schema(description = "Details of an organizational unit")
public record OrganizationalUnitResponse(
        @Schema(description = "Unique identifier", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID id,

        @Schema(description = "Name of the unit", example = "Core Engineering")
        String name,

        @Schema(description = "Type of unit", example = "AREA")
        String ouType,

        @Schema(description = "Designated owner corporate keys", example = "[\"CK0001\"]")
        Set<String> owners,

        @Schema(description = "Parent unit UUID (null for root unit)", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID parentId,

        @Schema(description = "Direct child unit UUIDs", example = "[\"b1eebc99-9c0b-4ef8-bb6d-6bb9bd380a22\"]")
        Set<UUID> childIds,

        @Schema(description = "Subtree child units (if subtree hydration was requested)")
        Set<OrganizationalUnitResponse> loadedChildren) {

    public static OrganizationalUnitResponse fromDomain(OrganizationalUnit unit) {
        if (unit == null) {
            return null;
        }

        final var owners = unit.owners().stream()
                .map(CorporateKey::value)
                .collect(Collectors.toSet());

        final var parentId = unit.parentId() != null ? unit.parentId().value() : null;

        final var childIds = unit.childIds().stream()
                .map(OuId::value)
                .collect(Collectors.toSet());

        final var loadedChildren = unit.loadedChildren().stream()
                .map(OrganizationalUnitResponse::fromDomain)
                .collect(Collectors.toSet());

        return new OrganizationalUnitResponse(
                unit.id().value(),
                unit.name().value(),
                unit.type().name(),
                owners,
                parentId,
                childIds,
                loadedChildren);
    }
}
