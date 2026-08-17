package com.example.oulearning.organization.infrastructure.persistence;

import java.time.Instant;

/**
 * Persistence entity representing an organization snapshot in the database.
 */
public record OrganizationSnapshotEntity(
        String id,
        String rootOuId,
        String status,
        Instant createdAt,
        Long version) {}
