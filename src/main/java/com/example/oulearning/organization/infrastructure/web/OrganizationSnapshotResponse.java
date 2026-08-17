package com.example.oulearning.organization.infrastructure.web;

import com.example.oulearning.organization.domain.organization.Organization;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/**
 * REST Response DTO representing an {@link Organization} aggregate root snapshot.
 */
@Schema(description = "Representation of an organization hierarchy snapshot")
public record OrganizationSnapshotResponse(
        @Schema(description = "Snapshot UUID", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID snapshotId,

        @Schema(description = "Snapshot status (ACTIVE or ARCHIVED)", example = "ACTIVE")
        String status,

        @Schema(description = "Hierarchical structure of organizational units starting from root")
        OrganizationalUnitResponse rootOu,

        @Schema(description = "Total number of organizational units across all levels in this snapshot", example = "42")
        int totalOusCount,

        @Schema(description = "Maximum depth of the organizational hierarchy tree", example = "4")
        int depth,

        @Schema(description = "Creation timestamp of the snapshot", example = "2026-08-17T12:00:00Z")
        Instant createdAt) {

    public static OrganizationSnapshotResponse fromDomain(Organization organization) {
        if (organization == null) {
            return null;
        }

        return new OrganizationSnapshotResponse(
                organization.snapshotId().value(),
                organization.status().name(),
                OrganizationalUnitResponse.fromDomain(organization.rootOu()),
                organization.totalOusCount(),
                organization.depth(),
                organization.createdAt());
    }
}
