package com.example.oulearning.organization.infrastructure.persistence.unit;

import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuType;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * Specific mapper responsible for bidirectional mapping between {@link OrganizationalUnit} domain objects
 * and {@link OrganizationalUnitEntity} persistence entities.
 */
@Component
public class OrganizationalUnitEntityMapper {

    /**
     * Maps an {@link OrganizationalUnit} domain model to a persistence entity.
     *
     * @param domain     the domain model
     * @param snapshotId optional snapshot ID
     * @param version    the optimistic locking version
     * @return the persistence entity
     */
    public OrganizationalUnitEntity toEntity(OrganizationalUnit domain, String snapshotId, Long version) {
        Objects.requireNonNull(domain, "OrganizationalUnit domain model cannot be null");
        return new OrganizationalUnitEntity(
                domain.id().toString(),
                domain.name().value(),
                domain.type().name(),
                snapshotId,
                version != null ? version : 0L);
    }

    /**
     * Maps a persistence entity and its loaded associations to an {@link OrganizationalUnit} domain model.
     *
     * @param entity         the persistence entity
     * @param owners         the corporate keys of unit owners
     * @param parentIds      the parent OU IDs
     * @param childIds       the child OU IDs
     * @param loadedChildren the loaded child domain units
     * @return the reconstructed {@link OrganizationalUnit} domain model
     */
    public OrganizationalUnit toDomain(
            OrganizationalUnitEntity entity,
            Set<CorporateKey> owners,
            Set<OuId> parentIds,
            Set<OuId> childIds,
            Set<OrganizationalUnit> loadedChildren) {
        Objects.requireNonNull(entity, "OrganizationalUnitEntity cannot be null");

        final var id = OuId.of(entity.id());
        final var name = OuName.of(entity.name());
        final var type = OuType.valueOf(entity.ouType());

        final var safeOwners = owners != null ? owners : Set.<CorporateKey>of();
        final var safeParents = parentIds != null ? parentIds : Set.<OuId>of();
        final var safeChildren = childIds != null ? childIds : Set.<OuId>of();
        final var safeLoadedChildren = loadedChildren != null ? loadedChildren : Set.<OrganizationalUnit>of();

        return new OrganizationalUnit(id, name, type, safeOwners, safeParents, safeChildren, safeLoadedChildren);
    }
}
