package com.example.oulearning.organization.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.organization.repository.OrganizationRepository;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrganizationApplicationServicesTest {

    private InMemoryUnitRepository unitRepository;
    private InMemoryOrganizationRepository organizationRepository;

    private CreateOrganizationalUnitService createUnitService;
    private GetOrganizationalUnitService getUnitService;
    private CreateOrganizationSnapshotService createSnapshotService;
    private GetLatestOrganizationService getLatestService;
    private GetOrganizationSnapshotService getSnapshotService;
    private GetOrganizationHistoryService getHistoryService;

    @BeforeEach
    void setUp() {
        unitRepository = new InMemoryUnitRepository();
        organizationRepository = new InMemoryOrganizationRepository();

        createUnitService = new CreateOrganizationalUnitService(unitRepository);
        getUnitService = new GetOrganizationalUnitService(unitRepository);
        createSnapshotService = new CreateOrganizationSnapshotService(organizationRepository, unitRepository);
        getLatestService = new GetLatestOrganizationService(organizationRepository);
        getSnapshotService = new GetOrganizationSnapshotService(organizationRepository);
        getHistoryService = new GetOrganizationHistoryService(organizationRepository);
    }

    @Test
    @DisplayName("should create and retrieve organizational unit")
    void should_createAndRetrieveUnit() {
        final var unitId = UUID.randomUUID();
        final var command = new CreateOrganizationalUnitCommand(
                unitId, "Backend Team", "SUBAREA", Set.of("CK0001"), Set.of(), Set.of());

        final var createdId = createUnitService.execute(command);
        assertThat(createdId).isEqualTo(unitId);

        final var retrieved = getUnitService.execute(GetOrganizationalUnitQuery.byId(unitId, false));
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().name().value()).isEqualTo("Backend Team");
    }

    @Test
    @DisplayName("should create snapshot and retrieve latest")
    void should_createSnapshotAndGetLatest() {
        final var unitId = UUID.randomUUID();
        final var unit = OrganizationalUnit.leaf(OuId.of(unitId), OuName.of("Root OU"), Set.of(), Set.of());
        unitRepository.save(unit);

        final var snapshotId = UUID.randomUUID();
        final var now = Instant.now();
        final var command = new CreateOrganizationSnapshotCommand(snapshotId, unitId, now);

        final var createdSnapshotId = createSnapshotService.execute(command);
        assertThat(createdSnapshotId).isEqualTo(snapshotId);

        final var latest = getLatestService.execute();
        assertThat(latest).isPresent();
        assertThat(latest.get().snapshotId().value()).isEqualTo(snapshotId);

        final var byId = getSnapshotService.execute(GetOrganizationSnapshotQuery.byId(snapshotId));
        assertThat(byId).isPresent();

        final var history = getHistoryService.execute();
        assertThat(history).hasSize(1);
    }

    @Test
    @DisplayName("should throw NoSuchElementException when creating snapshot for non-existent root OU")
    void should_throw_whenRootOuNotFound() {
        final var missingId = UUID.randomUUID();
        final var command = new CreateOrganizationSnapshotCommand(UUID.randomUUID(), missingId, Instant.now());

        assertThatThrownBy(() -> createSnapshotService.execute(command))
                .isInstanceOf(NoSuchElementException.class);
    }

    static class InMemoryUnitRepository implements OrganizationalUnitRepository {
        private final Map<OuId, OrganizationalUnit> store = new HashMap<>();

        @Override
        public Optional<OrganizationalUnit> find(OuSearchCriteria criteria) {
            if (criteria.findId().isPresent()) {
                return Optional.ofNullable(store.get(criteria.findId().get()));
            }
            if (criteria.findName().isPresent()) {
                return store.values().stream()
                        .filter(u -> u.name().equals(criteria.findName().get()))
                        .findFirst();
            }
            return Optional.empty();
        }

        @Override
        public void save(OrganizationalUnit unit) {
            store.put(unit.id(), unit);
        }
    }

    static class InMemoryOrganizationRepository implements OrganizationRepository {
        private final List<Organization> history = new ArrayList<>();

        @Override
        public void save(Organization organization) {
            history.add(organization);
        }

        @Override
        public Optional<Organization> findLatest() {
            return history.isEmpty() ? Optional.empty() : Optional.of(history.get(history.size() - 1));
        }

        @Override
        public Optional<Organization> findBySnapshotId(SnapshotId snapshotId) {
            return history.stream().filter(o -> o.snapshotId().equals(snapshotId)).findFirst();
        }

        @Override
        public Optional<Organization> findAt(Instant timestamp) {
            return history.stream().filter(o -> !o.createdAt().isAfter(timestamp)).reduce((a, b) -> b);
        }

        @Override
        public List<Organization> findAllHistory() {
            return List.copyOf(history);
        }
    }
}
