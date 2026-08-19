package com.example.oulearning.organization.infrastructure.persistence.unit;

/**
 * Persistence entity representing a row in the ORGANIZATIONAL_UNITS table.
 */
public record OrganizationalUnitEntity(
        String id,
        String name,
        String ouType,
        String snapshotId,
        String parentOuId,
        Long version) {}
