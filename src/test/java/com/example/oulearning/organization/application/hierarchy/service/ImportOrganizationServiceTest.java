package com.example.oulearning.organization.application.hierarchy.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.oulearning.organization.application.hierarchy.port.in.model.ParsedEmployeeRecord;
import com.example.oulearning.organization.application.hierarchy.port.out.OrganizationDocumentParser;
import com.example.oulearning.organization.domain.employee.model.Email;
import com.example.oulearning.organization.domain.employee.model.Employee;
import com.example.oulearning.organization.domain.employee.model.EmployeeId;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.hierarchy.model.Name;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ImportOrganizationServiceTest {

    private final OrganizationDocumentParser documentParser = mock(OrganizationDocumentParser.class);
    private final FakeEmployeeRepository employeeRepository = new FakeEmployeeRepository();
    private final FakeOrganizationalUnitRepository organizationalUnitRepository = new FakeOrganizationalUnitRepository();
    private final AtomicLong employeeIdSequence = new AtomicLong(100);
    private final AtomicLong ouIdSequence = new AtomicLong(200);

    private ImportOrganizationService service;

    @BeforeEach
    void setUp() {
        service = new ImportOrganizationService(
                documentParser,
                employeeRepository,
                organizationalUnitRepository,
                employeeIdSequence::incrementAndGet,
                ouIdSequence::incrementAndGet);
    }

    @Test
    @DisplayName("given new organization excel, when importing, then creates employees and hierarchy with roles")
    void givenNewOrganizationExcel_whenImporting_thenCreatesEmployeesAndHierarchyWithRoles() {
        // given
        final var records = List.of(
                new ParsedEmployeeRecord(
                        "Jane",
                        "Doe",
                        "jane.doe@example.com",
                        true,
                        List.of("Global", "Corporate", "Finance", "Accounting")),
                new ParsedEmployeeRecord(
                        "John",
                        "Smith",
                        "john.smith@example.com",
                        false,
                        List.of("Global", "Corporate", "Finance", "Accounting")));
        when(documentParser.parse(any())).thenReturn(records);

        // when
        final var result = service.execute(new ByteArrayInputStream(new byte[0]));

        // then
        assertThat(result.employeesProcessed()).isEqualTo(2);
        assertThat(result.organizationalUnitsProcessed()).isEqualTo(4);
        assertThat(result.employees()).hasSize(2);
        assertThat(result.organizationalUnits()).hasSize(4);

        final var jane = employeeRepository.findByEmail(Email.of("jane.doe@example.com")).orElseThrow();
        final var john = employeeRepository.findByEmail(Email.of("john.smith@example.com")).orElseThrow();

        assertThat(result.employees()).containsExactlyInAnyOrder(jane, john);

        final var globalOu = organizationalUnitRepository.findByNameAndParentId(Name.of("Global"), Optional.empty()).orElseThrow();
        final var corporateOu = organizationalUnitRepository.findByNameAndParentId(Name.of("Corporate"), Optional.of(globalOu.id())).orElseThrow();
        final var financeOu = organizationalUnitRepository.findByNameAndParentId(Name.of("Finance"), Optional.of(corporateOu.id())).orElseThrow();
        final var accountingOu = organizationalUnitRepository.findByNameAndParentId(Name.of("Accounting"), Optional.of(financeOu.id())).orElseThrow();

        assertThat(result.organizationalUnits()).contains(globalOu, corporateOu, financeOu, accountingOu);
        assertThat(accountingOu.members()).contains(jane.id(), john.id());
        assertThat(accountingOu.owners()).contains(jane.id());
        assertThat(accountingOu.owners()).doesNotContain(john.id());
    }

    @Test
    @DisplayName("given same organization excel twice, when importing, then is idempotent and no duplicate entities created")
    void givenSameOrganizationExcelTwice_whenImporting_thenIsIdempotentAndNoDuplicateEntitiesCreated() {
        // given
        final var records = List.of(
                new ParsedEmployeeRecord(
                        "Jane",
                        "Doe",
                        "jane.doe@example.com",
                        true,
                        List.of("Global", "Corporate", "Finance")));
        when(documentParser.parse(any())).thenReturn(records);

        // when
        final var firstResult = service.execute(new ByteArrayInputStream(new byte[0]));
        final var secondResult = service.execute(new ByteArrayInputStream(new byte[0]));

        // then
        assertThat(firstResult.employeesProcessed()).isEqualTo(1);
        assertThat(secondResult.employeesProcessed()).isEqualTo(1);
        assertThat(firstResult.employees()).hasSize(1);
        assertThat(firstResult.organizationalUnits()).hasSize(3);
        assertThat(secondResult.employees()).hasSize(1);
        assertThat(secondResult.organizationalUnits()).hasSize(3);
        assertThat(employeeRepository.employees.size()).isEqualTo(1);
        assertThat(organizationalUnitRepository.units.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("given incremental changes in excel, when importing, then merges changes and keeps previous data")
    void givenIncrementalChangesInExcel_whenImporting_thenMergesChangesAndKeepsPreviousData() {
        // given
        final var initialRecords = List.of(
                new ParsedEmployeeRecord(
                        "Jane",
                        "Doe",
                        "jane.doe@example.com",
                        true,
                        List.of("Global", "Engineering")));
        when(documentParser.parse(any())).thenReturn(initialRecords);
        service.execute(new ByteArrayInputStream(new byte[0]));

        final var incrementalRecords = List.of(
                new ParsedEmployeeRecord(
                        "Jane",
                        "Doe",
                        "jane.doe@example.com",
                        true,
                        List.of("Global", "Engineering")),
                new ParsedEmployeeRecord(
                        "Bob",
                        "Builder",
                        "bob.builder@example.com",
                        false,
                        List.of("Global", "Engineering", "DevOps")));
        when(documentParser.parse(any())).thenReturn(incrementalRecords);

        // when
        final var secondResult = service.execute(new ByteArrayInputStream(new byte[0]));

        // then
        assertThat(secondResult.employeesProcessed()).isEqualTo(2);
        assertThat(secondResult.employees()).hasSize(2);
        assertThat(secondResult.organizationalUnits()).hasSize(3);
        assertThat(employeeRepository.employees.size()).isEqualTo(2);
        assertThat(organizationalUnitRepository.units.size()).isEqualTo(3);

        final var jane = employeeRepository.findByEmail(Email.of("jane.doe@example.com")).orElseThrow();
        final var bob = employeeRepository.findByEmail(Email.of("bob.builder@example.com")).orElseThrow();

        assertThat(secondResult.employees()).containsExactlyInAnyOrder(jane, bob);

        final var global = organizationalUnitRepository.findByNameAndParentId(Name.of("Global"), Optional.empty()).orElseThrow();
        final var engineering = organizationalUnitRepository.findByNameAndParentId(Name.of("Engineering"), Optional.of(global.id())).orElseThrow();
        final var devOps = organizationalUnitRepository.findByNameAndParentId(Name.of("DevOps"), Optional.of(engineering.id())).orElseThrow();

        assertThat(secondResult.organizationalUnits()).contains(global, engineering, devOps);
        assertThat(engineering.members()).contains(jane.id());
        assertThat(engineering.owners()).contains(jane.id());
        assertThat(devOps.members()).contains(bob.id());
    }

    private static class FakeEmployeeRepository implements EmployeeRepository {
        final Map<EmployeeId, Employee> employees = new HashMap<>();

        @Override
        public Optional<Employee> findById(final EmployeeId id) {
            return Optional.ofNullable(employees.get(id));
        }

        @Override
        public Optional<Employee> findByEmail(final Email email) {
            return employees.values().stream()
                    .filter(e -> e.email().value().equalsIgnoreCase(email.value()))
                    .findFirst();
        }

        @Override
        public void save(final Employee employee) {
            employees.put(employee.id(), employee);
        }
    }

    private static class FakeOrganizationalUnitRepository implements OrganizationalUnitRepository {
        final Map<OrganizationalUnitId, OrganizationalUnit> units = new HashMap<>();

        @Override
        public Optional<OrganizationalUnit> findById(final OrganizationalUnitId id) {
            return Optional.ofNullable(units.get(id));
        }

        @Override
        public Optional<OrganizationalUnit> findByNameAndParentId(
                final Name name, final Optional<OrganizationalUnitId> parentId) {
            return units.values().stream()
                    .filter(u -> u.name().value().equalsIgnoreCase(name.value()) && u.parentId().equals(parentId))
                    .findFirst();
        }

        @Override
        public List<OrganizationalUnit> findSubtreeById(final OrganizationalUnitId id) {
            return List.of();
        }

        @Override
        public List<OrganizationalUnit> findAll() {
            return List.copyOf(units.values());
        }

        @Override
        public void save(final OrganizationalUnit organizationalUnit) {
            units.put(organizationalUnit.id(), organizationalUnit);
        }
    }
}
