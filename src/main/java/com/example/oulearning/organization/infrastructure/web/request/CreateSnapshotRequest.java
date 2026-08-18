package com.example.oulearning.organization.infrastructure.web.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/**
 * REST Request DTO for taking an organization hierarchy snapshot.
 */
@Schema(description = "Payload for taking an organization hierarchy snapshot")
public record CreateSnapshotRequest(
        @Schema(description = "Optional snapshot UUID (auto-generated if omitted)", example = "c2eebc99-9c0b-4ef8-bb6d-6bb9bd380a33")
        UUID snapshotId,

        @NotNull(message = "Root OU ID cannot be null")
        @Schema(description = "UUID of the root organizational unit", example = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11")
        UUID rootOuId,

        @Schema(description = "Optional timestamp (defaults to current time if omitted)", example = "2026-08-17T22:00:00Z")
        Instant createdAt) {}
