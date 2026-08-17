package com.example.oulearning.organization.infrastructure.persistence;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence adapter implementing {@link OrganizationalUnitRepository} using MyBatis and {@link OrganizationalUnitEntityMapper}.
 */
@Repository
public class OrganizationalUnitPersistenceAdapter implements OrganizationalUnitRepository {

    private final OrganizationalUnitMyBatisMapper mapper;
    private final OrganizationalUnitEntityMapper entityMapper;

    public OrganizationalUnitPersistenceAdapter(
            OrganizationalUnitMyBatisMapper mapper,
            OrganizationalUnitEntityMapper entityMapper) {
        this.mapper = Objects.requireNonNull(mapper, "OrganizationalUnitMyBatisMapper cannot be null");
        this.entityMapper = Objects.requireNonNull(entityMapper, "OrganizationalUnitEntityMapper cannot be null");
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizationalUnit> find(OuSearchCriteria criteria) {
        Objects.requireNonNull(criteria, "Search criteria cannot be null");

        OrganizationalUnitEntity entity = null;
        if (criteria.findId().isPresent()) {
            entity = mapper.findUnitById(criteria.findId().get().toString());
        } else if (criteria.findName().isPresent()) {
            entity = mapper.findUnitByName(criteria.findName().get().value());
        }

        if (entity == null) {
            return Optional.empty();
        }

        return Optional.of(hydrateUnit(entity, criteria.includeSubtree()));
    }

    @Override
    @Transactional
    public void save(OrganizationalUnit unit) {
        Objects.requireNonNull(unit, "OrganizationalUnit cannot be null");
        saveInternal(unit, null);
    }

    @Transactional
    public void saveWithSnapshot(OrganizationalUnit unit, String snapshotId) {
        Objects.requireNonNull(unit, "OrganizationalUnit cannot be null");
        saveInternal(unit, snapshotId);
    }

    private void saveInternal(OrganizationalUnit unit, String snapshotId) {
        final var unitIdStr = unit.id().toString();
        final var existing = mapper.findUnitById(unitIdStr);

        final var targetSnapshotId = snapshotId != null ? snapshotId : (existing != null ? existing.snapshotId() : null);
        final var version = existing != null ? existing.version() : 0L;

        final var entity = entityMapper.toEntity(unit, targetSnapshotId, version);

        if (existing == null) {
            mapper.insertUnit(entity);
        } else {
            mapper.updateUnit(entity);
            mapper.deleteOwnersByOuId(unitIdStr);
            mapper.deleteParentsByOuId(unitIdStr);
            mapper.deleteChildrenByOuId(unitIdStr);
        }

        // Save owners
        for (final var owner : unit.owners()) {
            mapper.insertOwner(unitIdStr, owner.value());
        }

        // Save parents
        for (final var parentId : unit.parentIds()) {
            mapper.insertParent(unitIdStr, parentId.toString());
        }

        // Save children
        for (final var childId : unit.childIds()) {
            mapper.insertChild(unitIdStr, childId.toString());
        }

        // Save loaded children recursively
        for (final var childUnit : unit.loadedChildren()) {
            saveInternal(childUnit, snapshotId);
        }
    }

    private OrganizationalUnit hydrateUnit(OrganizationalUnitEntity entity, boolean includeSubtree) {
        final var unitIdStr = entity.id();

        final var ownerKeys = mapper.findOwnersByOuId(unitIdStr);
        final var owners = ownerKeys == null
                ? Set.<CorporateKey>of()
                : ownerKeys.stream().map(CorporateKey::of).collect(Collectors.toSet());

        final var parentIdStrs = mapper.findParentsByOuId(unitIdStr);
        final var parentIds = parentIdStrs == null
                ? Set.<OuId>of()
                : parentIdStrs.stream().map(OuId::of).collect(Collectors.toSet());

        final var childIdStrs = mapper.findChildrenByOuId(unitIdStr);
        final var childIds = childIdStrs == null
                ? Set.<OuId>of()
                : childIdStrs.stream().map(OuId::of).collect(Collectors.toSet());

        final var loadedChildren = new HashSet<OrganizationalUnit>();
        if (includeSubtree && !childIds.isEmpty()) {
            for (final var childIdStr : childIdStrs) {
                final var childEntity = mapper.findUnitById(childIdStr);
                if (childEntity != null) {
                    loadedChildren.add(hydrateUnit(childEntity, true));
                }
            }
        }

        return entityMapper.toDomain(entity, owners, parentIds, childIds, Set.copyOf(loadedChildren));
    }
}
