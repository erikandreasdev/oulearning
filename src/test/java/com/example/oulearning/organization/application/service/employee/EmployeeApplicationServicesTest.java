package com.example.oulearning.organization.application.service.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey;
import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.OuType;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import java.util.Collection;
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
import com.example.oulearning.organization.application.port.in.query.GetEmployeeQuery;
import com.example.oulearning.organization.application.port.in.query.GetEmployeesByOuQuery;
import com.example.oulearning.organization.application.port.in.command.RegisterEmployeeCommand;

class EmployeeApplicationServicesTest {

    private InMemoryEmployeeRepository employeeRepository;
    private InMemoryUnitRepository unitRepository;

    private RegisterEmployeeService registerService;
    private GetEmployeeService getEmployeeService;
    private GetEmployeesByOuService getEmployeesByOuService;

    @BeforeEach
    void setUp() {
        employeeRepository = new InMemoryEmployeeRepository();
        unitRepository = new InMemoryUnitRepository();

        registerService = new RegisterEmployeeService(employeeRepository, unitRepository);
        getEmployeeService = new GetEmployeeService(employeeRepository);
        getEmployeesByOuService = new GetEmployeesByOuService(employeeRepository, unitRepository);
    }

    @Test
    @DisplayName("should register employee and retrieve by corporate key")
    void should_registerAndRetrieveEmployee() {
        final var ouId = UUID.randomUUID();
        final var ou = OrganizationalUnit.leaf(OuId.of(ouId), OuName.of("Engineering"), Set.of(), null);
        unitRepository.save(ou);

        final var command = new RegisterEmployeeCommand(
                "CK0001", "Alice", "Smith", "alice@example.com", "+34911223344", "MANAGER", ouId);

        final var key = registerService.execute(command);
        assertThat(key).isEqualTo("CK0001");

        final var retrieved = getEmployeeService.execute(new GetEmployeeQuery("CK0001"));
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().corporateKey().value()).isEqualTo("CK0001");
        assertThat(retrieved.get().fullName().name().value()).isEqualTo("Alice");
        assertThat(retrieved.get().phone().value()).isEqualTo("+34911223344");
        assertThat(retrieved.get().ouId().value()).isEqualTo(ouId);
    }

    @Test
    @DisplayName("should throw NoSuchElementException when registering employee to non-existent OU")
    void should_throw_whenTargetOuNotFound() {
        final var missingOuId = UUID.randomUUID();
        final var command = new RegisterEmployeeCommand(
                "CK0001", "Alice", "Smith", "alice@example.com", null, "MANAGER", missingOuId);

        assertThatThrownBy(() -> registerService.execute(command))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("OrganizationalUnit with ID");
    }

    @Test
    @DisplayName("should retrieve direct employees vs subtree employees recursively")
    void should_retrieveEmployeesByOu_directAndSubtree() {
        // Hierarchy: Parent (Area) -> Child1 (Leaf), Child2 (Leaf)
        final var child1Id = OuId.of(UUID.randomUUID());
        final var child2Id = OuId.of(UUID.randomUUID());
        final var parentId = OuId.of(UUID.randomUUID());

        final var child1 = OrganizationalUnit.leaf(child1Id, OuName.of("Frontend"), Set.of(), parentId);
        final var child2 = OrganizationalUnit.leaf(child2Id, OuName.of("Backend"), Set.of(), parentId);
        final var parent = OrganizationalUnit.withChildren(
                parentId, OuName.of("Engineering"), OuType.AREA, Set.of(), null, Set.of(child1, child2));

        unitRepository.save(parent);
        unitRepository.save(child1);
        unitRepository.save(child2);

        // Register employees
        registerService.execute(new RegisterEmployeeCommand(
                "CK0001", "Alice", "Parent", "alice@corp.com", null, "MANAGER", parentId.value()));
        registerService.execute(new RegisterEmployeeCommand(
                "CK0002", "Bob", "Frontend", "bob@corp.com", null, "EMPLOYEE", child1Id.value()));
        registerService.execute(new RegisterEmployeeCommand(
                "CK0003", "Charlie", "Backend", "charlie@corp.com", null, "EMPLOYEE", child2Id.value()));

        // 1. Direct query on Parent OU (includeSubtree = false)
        final var directEmployees = getEmployeesByOuService.execute(
                GetEmployeesByOuQuery.byId(parentId.value(), false));
        assertThat(directEmployees).hasSize(1);
        assertThat(directEmployees.get(0).corporateKey().value()).isEqualTo("CK0001");

        // 2. Subtree query on Parent OU (includeSubtree = true)
        final var subtreeEmployees = getEmployeesByOuService.execute(
                GetEmployeesByOuQuery.byId(parentId.value(), true));
        assertThat(subtreeEmployees).hasSize(3);

        // 3. Subtree query by name
        final var byNameSubtree = getEmployeesByOuService.execute(
                GetEmployeesByOuQuery.byName("Engineering", true));
        assertThat(byNameSubtree).hasSize(3);
    }

    static class InMemoryEmployeeRepository implements EmployeeRepository {
        private final Map<CorporateKey, Employee> store = new HashMap<>();

        @Override
        public Optional<Employee> findByCorporateKey(CorporateKey corporateKey) {
            return Optional.ofNullable(store.get(corporateKey));
        }

        @Override
        public List<Employee> findByOuId(OuId ouId) {
            return store.values().stream().filter(e -> e.ouId().equals(ouId)).toList();
        }

        @Override
        public List<Employee> findByOuIds(Collection<OuId> ouIds) {
            return store.values().stream().filter(e -> ouIds.contains(e.ouId())).toList();
        }

        @Override
        public void save(Employee employee) {
            store.put(employee.corporateKey(), employee);
        }

        @Override
        public void delete(CorporateKey corporateKey) {
            store.remove(corporateKey);
        }
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
}
