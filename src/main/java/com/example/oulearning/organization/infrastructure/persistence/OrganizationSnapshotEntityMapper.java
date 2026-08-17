package com.example.oulearning.organization.infrastructure.persistence;

import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.organization.SnapshotStatus;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Dedicated mapper for bidirectional mapping between {@link Organization} domain aggregates
 * and {@link OrganizationSnapshotEntity} persistence records.
 */
@Component
public class OrganizationSnapshotEntityMapper {

    public OrganizationSnapshotEntity toEntity(Organization domain, Long version) {
        Objects.requireNonNull(domain, "Organization domain model cannot be null");

        return new OrganizationSnapshotEntity(
                domain.snapshotId().toString(),
                domain.rootOu().id().toString(),
                domain.status().name(),
                domain.createdAt(),
                version != null ? version : 0L);
    }

    public Organization toDomain(OrganizationSnapshotEntity entity, OrganizationalUnit rootOu) {
        Objects.requireNonNull(entity, "OrganizationSnapshotEntity cannot be null");
        Objects.requireNonNull(rootOu, "Root OrganizationalUnit cannot be null");

        final var snapshotId = SnapshotId.of(entity.id());
        final var status = entity.status() != null ? SnapshotStatus.valueOf(entity.status()) : SnapshotStatus.ACTIVE;
        return new Organization(snapshotId, rootOu, status, entity.createdAt());
    }
}
