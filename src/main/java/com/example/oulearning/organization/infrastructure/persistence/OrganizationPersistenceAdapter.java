package com.example.oulearning.organization.infrastructure.persistence;

import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.organization.repository.OrganizationRepository;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persistence adapter implementing {@link OrganizationRepository} with MyBatis, {@link OrganizationSnapshotEntityMapper},
 * and in-memory caching for high-performance latest snapshot retrieval.
 */
@Repository
public class OrganizationPersistenceAdapter implements OrganizationRepository {

    private final OrganizationSnapshotMyBatisMapper snapshotMapper;
    private final OrganizationSnapshotEntityMapper entityMapper;
    private final OrganizationalUnitRepository unitRepository;
    private final AtomicReference<Organization> latestSnapshotCache = new AtomicReference<>();

    public OrganizationPersistenceAdapter(
            OrganizationSnapshotMyBatisMapper snapshotMapper,
            OrganizationSnapshotEntityMapper entityMapper,
            OrganizationalUnitRepository unitRepository) {
        this.snapshotMapper = Objects.requireNonNull(snapshotMapper, "OrganizationSnapshotMyBatisMapper cannot be null");
        this.entityMapper = Objects.requireNonNull(entityMapper, "OrganizationSnapshotEntityMapper cannot be null");
        this.unitRepository = Objects.requireNonNull(unitRepository, "OrganizationalUnitRepository cannot be null");
    }

    @Override
    @Transactional
    public void save(Organization organization) {
        Objects.requireNonNull(organization, "Organization cannot be null");

        final var snapshotEntity = entityMapper.toEntity(organization, 0L);
        snapshotMapper.insertSnapshot(snapshotEntity);

        // Save root OU and entire hierarchy
        unitRepository.save(organization.rootOu());

        // Refresh latest snapshot cache
        latestSnapshotCache.set(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Organization> findLatest() {
        // Fast path: return from memory cache if available
        final var cached = latestSnapshotCache.get();
        if (cached != null) {
            return Optional.of(cached);
        }

        // Slow path: load from database and populate cache
        final var snapshotEntity = snapshotMapper.findLatestSnapshot();
        if (snapshotEntity == null) {
            return Optional.empty();
        }

        final var organization = hydrateOrganization(snapshotEntity);
        latestSnapshotCache.set(organization);
        return Optional.of(organization);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Organization> findBySnapshotId(SnapshotId snapshotId) {
        Objects.requireNonNull(snapshotId, "SnapshotId cannot be null");

        final var snapshotEntity = snapshotMapper.findSnapshotById(snapshotId.toString());
        if (snapshotEntity == null) {
            return Optional.empty();
        }

        return Optional.of(hydrateOrganization(snapshotEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Organization> findAt(Instant timestamp) {
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");

        final var snapshotEntity = snapshotMapper.findSnapshotAt(timestamp);
        if (snapshotEntity == null) {
            return Optional.empty();
        }

        return Optional.of(hydrateOrganization(snapshotEntity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organization> findAllHistory() {
        final var entities = snapshotMapper.findAllSnapshots();
        if (entities == null || entities.isEmpty()) {
            return List.of();
        }

        final var result = new ArrayList<Organization>(entities.size());
        for (final var entity : entities) {
            result.add(hydrateOrganization(entity));
        }

        return List.copyOf(result);
    }

    private Organization hydrateOrganization(OrganizationSnapshotEntity entity) {
        final var rootOuId = OuId.of(entity.rootOuId());
        final var rootOu = unitRepository
                .find(OuSearchCriteria.byId(rootOuId, true))
                .orElseThrow(() -> new IllegalStateException(
                        "Corrupted organization snapshot '%s': root OU '%s' not found in database"
                                .formatted(entity.id(), entity.rootOuId())));

        return entityMapper.toDomain(entity, rootOu);
    }

    // Visible for testing cache behavior
    void clearCache() {
        latestSnapshotCache.set(null);
    }
}
