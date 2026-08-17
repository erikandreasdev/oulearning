package com.example.oulearning.organization.infrastructure.persistence;

import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Specific mapper responsible for bidirectional mapping between {@link Organization} domain snapshots
 * and {@link OrganizationSnapshotEntity} persistence entities.
 */
@Component
public class OrganizationSnapshotEntityMapper {

    /**
     * Maps an {@link Organization} domain model to a persistence entity.
     *
     * @param domain  the organization aggregate
     * @param version the optimistic locking version
     * @return the persistence entity
     */
    public OrganizationSnapshotEntity toEntity(Organization domain, Long version) {
        Objects.requireNonNull(domain, "Organization domain model cannot be null");
        return new OrganizationSnapshotEntity(
                domain.snapshotId().toString(),
                domain.rootOu().id().toString(),
                domain.createdAt(),
                version != null ? version : 0L);
    }

    /**
     * Maps a persistence entity and hydrated root OU to an {@link Organization} domain snapshot.
     *
     * @param entity the persistence entity
     * @param rootOu the fully materialized root organizational unit
     * @return the reconstructed {@link Organization} domain snapshot
     */
    public Organization toDomain(OrganizationSnapshotEntity entity, OrganizationalUnit rootOu) {
        Objects.requireNonNull(entity, "OrganizationSnapshotEntity cannot be null");
        Objects.requireNonNull(rootOu, "Root OrganizationalUnit cannot be null");

        return new Organization(
                SnapshotId.of(entity.id()),
                rootOu,
                entity.createdAt());
    }
}
