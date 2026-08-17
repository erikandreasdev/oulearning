package com.example.oulearning.organization.infrastructure.persistence;

/**
 * Persistence entity representing a row in the ORGANIZATIONAL_UNITS table.
 */
public record OrganizationalUnitEntity(
        String id,
        String name,
        String ouType,
        String snapshotId,
        Long version) {}
