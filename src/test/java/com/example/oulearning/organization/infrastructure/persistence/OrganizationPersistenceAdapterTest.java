package com.example.oulearning.organization.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class OrganizationPersistenceAdapterTest {

    private OrganizationSnapshotMyBatisMapper snapshotMapper;
    private OrganizationSnapshotEntityMapper entityMapper;
    private OrganizationalUnitPersistenceAdapter unitAdapter;
    private OrganizationPersistenceAdapter adapter;

    @BeforeEach
    void setUp() {
        snapshotMapper = mock(OrganizationSnapshotMyBatisMapper.class);
        entityMapper = new OrganizationSnapshotEntityMapper();
        unitAdapter = mock(OrganizationalUnitPersistenceAdapter.class);
        adapter = new OrganizationPersistenceAdapter(snapshotMapper, entityMapper, unitAdapter);
    }

    private Organization createSampleOrganization(SnapshotId snapshotId, Instant timestamp) {
        final var rootOu = OrganizationalUnit.leaf(
                OuId.of(UUID.randomUUID()),
                OuName.of("Root Org"),
                Set.of(CorporateKey.of("CK0001")),
                Set.of());
        return new Organization(snapshotId, rootOu, timestamp);
    }

    @Nested
    @DisplayName("Latest Snapshot Caching Mechanics")
    class LatestSnapshotCachingMechanics {

        @Test
        @DisplayName("should cache latest snapshot on save and serve subsequent findLatest calls from cache")
        void should_cacheLatestSnapshot_onSave() {
            final var snapshotId = SnapshotId.of(UUID.randomUUID());
            final var organization = createSampleOrganization(snapshotId, Instant.now());

            // Save organization -> updates cache
            adapter.save(organization);

            verify(snapshotMapper).insertSnapshot(any(OrganizationSnapshotEntity.class));
            verify(unitAdapter).saveWithSnapshot(organization.rootOu(), snapshotId.toString());

            // findLatest -> served from cache without querying database mapper
            final var latest = adapter.findLatest();

            assertThat(latest).contains(organization);
            verify(snapshotMapper, never()).findLatestSnapshot();
        }

        @Test
        @DisplayName("should query database on cache miss and populate cache")
        void should_queryDatabase_onCacheMiss() {
            final var snapshotIdStr = UUID.randomUUID().toString();
            final var rootOuId = OuId.of(UUID.randomUUID());
            final var timestamp = Instant.now();

            final var snapshotEntity = new OrganizationSnapshotEntity(
                    snapshotIdStr, rootOuId.toString(), timestamp, 0L);

            final var rootOu = OrganizationalUnit.leaf(
                    rootOuId,
                    OuName.of("Root Org"),
                    Set.of(CorporateKey.of("CK0001")),
                    Set.of());

            when(snapshotMapper.findLatestSnapshot()).thenReturn(snapshotEntity);
            when(unitAdapter.find(OuSearchCriteria.byId(rootOuId, true))).thenReturn(Optional.of(rootOu));

            // First call -> Cache Miss, queries DB
            final var firstCall = adapter.findLatest();
            assertThat(firstCall).isPresent();
            assertThat(firstCall.get().snapshotId().toString()).isEqualTo(snapshotIdStr);

            // Second call -> Cache Hit, does NOT query DB again
            final var secondCall = adapter.findLatest();
            assertThat(secondCall).isEqualTo(firstCall);

            verify(snapshotMapper, times(1)).findLatestSnapshot();
        }

        @Test
        @DisplayName("should invalidate and refresh cache when a new snapshot is saved")
        void should_refreshCache_whenNewSnapshotSaved() {
            final var org1 = createSampleOrganization(SnapshotId.of(UUID.randomUUID()), Instant.now().minusSeconds(60));
            final var org2 = createSampleOrganization(SnapshotId.of(UUID.randomUUID()), Instant.now());

            adapter.save(org1);
            assertThat(adapter.findLatest()).contains(org1);

            adapter.save(org2);
            assertThat(adapter.findLatest()).contains(org2);
        }
    }

    @Nested
    @DisplayName("Historical and Time-Travel Queries")
    class HistoricalAndTimeTravelQueries {

        @Test
        @DisplayName("should find snapshot at specific historical timestamp")
        void should_findSnapshotAt_historicalTimestamp() {
            final var targetTime = Instant.parse("2026-08-17T12:00:00Z");
            final var snapshotIdStr = UUID.randomUUID().toString();
            final var rootOuId = OuId.of(UUID.randomUUID());

            final var entity = new OrganizationSnapshotEntity(
                    snapshotIdStr, rootOuId.toString(), targetTime, 0L);
            final var rootOu = OrganizationalUnit.leaf(
                    rootOuId, OuName.of("Root Org"), Set.of(CorporateKey.of("CK0001")), Set.of());

            when(snapshotMapper.findSnapshotAt(targetTime)).thenReturn(entity);
            when(unitAdapter.find(OuSearchCriteria.byId(rootOuId, true))).thenReturn(Optional.of(rootOu));

            final var result = adapter.findAt(targetTime);

            assertThat(result).isPresent();
            assertThat(result.get().snapshotId().toString()).isEqualTo(snapshotIdStr);
        }

        @Test
        @DisplayName("should find snapshot by snapshot ID")
        void should_findBySnapshotId() {
            final var snapshotId = SnapshotId.of(UUID.randomUUID());
            final var rootOuId = OuId.of(UUID.randomUUID());

            final var entity = new OrganizationSnapshotEntity(
                    snapshotId.toString(), rootOuId.toString(), Instant.now(), 0L);
            final var rootOu = OrganizationalUnit.leaf(
                    rootOuId, OuName.of("Root Org"), Set.of(CorporateKey.of("CK0001")), Set.of());

            when(snapshotMapper.findSnapshotById(snapshotId.toString())).thenReturn(entity);
            when(unitAdapter.find(OuSearchCriteria.byId(rootOuId, true))).thenReturn(Optional.of(rootOu));

            final var result = adapter.findBySnapshotId(snapshotId);

            assertThat(result).isPresent();
            assertThat(result.get().snapshotId()).isEqualTo(snapshotId);
        }

        @Test
        @DisplayName("should retrieve all historical snapshots ordered chronologically")
        void should_findAllHistory_chronological() {
            final var id1 = UUID.randomUUID().toString();
            final var id2 = UUID.randomUUID().toString();
            final var rootId = OuId.of(UUID.randomUUID());

            final var e1 = new OrganizationSnapshotEntity(id1, rootId.toString(), Instant.now().minusSeconds(100), 0L);
            final var e2 = new OrganizationSnapshotEntity(id2, rootId.toString(), Instant.now(), 0L);

            final var rootOu = OrganizationalUnit.leaf(
                    rootId, OuName.of("Root Org"), Set.of(CorporateKey.of("CK0001")), Set.of());

            when(snapshotMapper.findAllSnapshots()).thenReturn(List.of(e1, e2));
            when(unitAdapter.find(OuSearchCriteria.byId(rootId, true))).thenReturn(Optional.of(rootOu));

            final var history = adapter.findAllHistory();

            assertThat(history).hasSize(2);
            assertThat(history.get(0).snapshotId().toString()).isEqualTo(id1);
            assertThat(history.get(1).snapshotId().toString()).isEqualTo(id2);
        }
    }
}
