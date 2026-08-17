package com.example.oulearning.organization.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.employee.Email;
import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.EmployeeRole;
import com.example.oulearning.organization.domain.employee.FullName;
import com.example.oulearning.organization.domain.employee.Name;
import com.example.oulearning.organization.domain.employee.Surname;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.organization.repository.OrganizationRepository;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import com.example.oulearning.organization.infrastructure.parser.EmployeeFileParser;
import com.example.oulearning.organization.infrastructure.parser.OrganizationFileParser;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
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
    private InMemoryEmployeeRepository employeeRepository;

    private CreateOrganizationalUnitService createUnitService;
    private GetOrganizationalUnitService getUnitService;
    private CreateOrganizationSnapshotService createSnapshotService;
    private GetLatestOrganizationService getLatestService;
    private GetOrganizationSnapshotService getSnapshotService;
    private GetOrganizationHistoryService getHistoryService;
    private UploadOrganizationSnapshotService uploadOrgService;
    private UploadEmployeesService uploadEmpService;

    private final Clock clock = Clock.fixed(Instant.parse("2026-08-17T22:00:00Z"), ZoneId.of("UTC"));

    @BeforeEach
    void setUp() {
        unitRepository = new InMemoryUnitRepository();
        organizationRepository = new InMemoryOrganizationRepository();
        employeeRepository = new InMemoryEmployeeRepository();

        createUnitService = new CreateOrganizationalUnitService(unitRepository);
        getUnitService = new GetOrganizationalUnitService(unitRepository);
        createSnapshotService = new CreateOrganizationSnapshotService(organizationRepository, unitRepository);
        getLatestService = new GetLatestOrganizationService(organizationRepository);
        getSnapshotService = new GetOrganizationSnapshotService(organizationRepository);
        getHistoryService = new GetOrganizationHistoryService(organizationRepository);

        final var orgParser = new OrganizationFileParser();
        final var empParser = new EmployeeFileParser();

        uploadOrgService = new UploadOrganizationSnapshotService(
                organizationRepository, employeeRepository, orgParser, empParser, clock);
        uploadEmpService = new UploadEmployeesService(
                organizationRepository, employeeRepository, empParser);
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

    @Test
    @DisplayName("should upload organization snapshot and employees from CSV files")
    void should_uploadOrganizationSnapshot_andEmployees_fromCsv() {
        // Register a Manager employee
        final var manager = Employee.of(
                CorporateKey.of("CK0001"),
                FullName.of(Name.of("Alice"), Surname.of("Manager")),
                Email.of("alice@company.com"),
                null,
                EmployeeRole.MANAGER,
                OuId.of(UUID.randomUUID()));
        employeeRepository.save(manager);

        final var orgCsv = """
                name,parent,type,owners
                CEO,,ORGANIZATION,CK0001
                Engineering,CEO,DEPARTMENT,CK0001
                Backend,Engineering,TEAM,CK0001
                """;

        final var empCsv = """
                corporateKey,firstName,lastName,email,role,ouName
                CK0002,Bob,Builder,bob@company.com,EMPLOYEE,Backend
                """;

        final var command = new UploadOrganizationSnapshotCommand(
                "CK0001",
                orgCsv.getBytes(StandardCharsets.UTF_8),
                "org.csv",
                empCsv.getBytes(StandardCharsets.UTF_8),
                "employees.csv");

        final var snapshotId = uploadOrgService.execute(command);
        assertThat(snapshotId).isNotNull();

        final var latest = getLatestService.execute();
        assertThat(latest).isPresent();
        assertThat(latest.get().snapshotId().value()).isEqualTo(snapshotId);
        assertThat(latest.get().totalOusCount()).isEqualTo(3);
        assertThat(latest.get().isActive()).isTrue();

        final var bob = employeeRepository.findByCorporateKey(CorporateKey.of("CK0002"));
        assertThat(bob).isPresent();
        assertThat(bob.get().fullName().name().value()).isEqualTo("Bob");
    }

    @Test
    @DisplayName("should upload employees batch for active snapshot")
    void should_uploadEmployeesBatch_forActiveSnapshot() {
        // Seed active organization
        final var rootOu = OrganizationalUnit.leaf(
                OuId.of(UUID.randomUUID()),
                OuName.of("Engineering"),
                Set.of(),
                Set.of());
        organizationRepository.save(Organization.active(SnapshotId.of(UUID.randomUUID()), rootOu, Instant.now(clock)));

        final var empCsv = """
                corporateKey,firstName,lastName,email,role,ouName
                CK0010,Carol,Coder,carol@company.com,EMPLOYEE,Engineering
                """;

        final var command = new UploadEmployeesCommand(
                null,
                empCsv.getBytes(StandardCharsets.UTF_8),
                "employees.csv");

        final var count = uploadEmpService.execute(command);
        assertThat(count).isEqualTo(1);

        assertThat(employeeRepository.findByCorporateKey(CorporateKey.of("CK0010"))).isPresent();
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when unauthorized non-manager tries to upload")
    void should_throw_whenNonManagerTriesToUpload() {
        final var regularEmp = Employee.of(
                CorporateKey.of("CK0099"),
                FullName.of(Name.of("Eve"), Surname.of("Employee")),
                Email.of("eve@company.com"),
                null,
                EmployeeRole.EMPLOYEE,
                OuId.of(UUID.randomUUID()));
        employeeRepository.save(regularEmp);

        final var orgCsv = "name,parent\nCEO,\n";
        final var command = new UploadOrganizationSnapshotCommand(
                "CK0099",
                orgCsv.getBytes(StandardCharsets.UTF_8),
                "org.csv",
                null,
                null);

        assertThatThrownBy(() -> uploadOrgService.execute(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not authorized");
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

    static class InMemoryEmployeeRepository implements EmployeeRepository {
        private final Map<CorporateKey, Employee> store = new HashMap<>();

        @Override
        public void save(Employee employee) {
            store.put(employee.corporateKey(), employee);
        }

        @Override
        public Optional<Employee> findByCorporateKey(CorporateKey corporateKey) {
            return Optional.ofNullable(store.get(corporateKey));
        }

        @Override
        public List<Employee> findByOuId(OuId ouId) {
            return store.values().stream()
                    .filter(e -> e.ouId().equals(ouId))
                    .toList();
        }

        @Override
        public List<Employee> findByOuIds(java.util.Collection<OuId> ouIds) {
            return store.values().stream()
                    .filter(e -> ouIds.contains(e.ouId()))
                    .toList();
        }

        @Override
        public void delete(CorporateKey corporateKey) {
            store.remove(corporateKey);
        }
    }
}
