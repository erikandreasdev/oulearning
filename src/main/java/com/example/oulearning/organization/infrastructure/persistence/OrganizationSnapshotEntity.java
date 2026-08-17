package com.example.oulearning.organization.infrastructure.persistence;

import java.time.Instant;

/**
 * Persistence entity representing a row in the ORGANIZATION_SNAPSHOTS table.
 */
public record OrganizationSnapshotEntity(
        String id,
        String rootOuId,
        Instant createdAt,
        Long version) {}
