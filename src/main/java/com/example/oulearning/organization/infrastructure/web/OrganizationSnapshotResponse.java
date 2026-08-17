package com.example.oulearning.organization.infrastructure.web;

import com.example.oulearning.organization.domain.organization.Organization;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/**
 * REST Response DTO representing an Organization hierarchy snapshot.
 */
@Schema(description = "Full organization hierarchy snapshot")
public record OrganizationSnapshotResponse(
        @Schema(description = "Unique snapshot identifier", example = "c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33")
        UUID snapshotId,

        @Schema(description = "Timestamp when the snapshot was taken", example = "2026-08-17T22:00:00Z")
        Instant createdAt,

        @Schema(description = "Root organizational unit with hydrated subtree")
        OrganizationalUnitResponse rootOu) {

    public static OrganizationSnapshotResponse fromDomain(Organization organization) {
        if (organization == null) {
            return null;
        }

        return new OrganizationSnapshotResponse(
                organization.snapshotId().value(),
                organization.createdAt(),
                OrganizationalUnitResponse.fromDomain(organization.rootOu()));
    }
}
