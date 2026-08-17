package com.example.oulearning.training.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.oulearning.organization.domain.employee.Email;
import com.example.oulearning.organization.domain.employee.Employee;
import com.example.oulearning.organization.domain.employee.EmployeeRole;
import com.example.oulearning.organization.domain.employee.FullName;
import com.example.oulearning.organization.domain.employee.Phone;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.training.application.port.out.TrainingBudgetPort;
import com.example.oulearning.training.domain.CorporateKey;
import com.example.oulearning.training.domain.OuId;
import com.example.oulearning.training.domain.TrainingRequest;
import com.example.oulearning.training.domain.TrainingRequestId;
import com.example.oulearning.training.domain.TrainingRequestStatus;
import com.example.oulearning.training.domain.exception.InvalidAssistantException;
import com.example.oulearning.training.domain.exception.UnauthorizedManagerException;
import com.example.oulearning.training.domain.exception.UnauthorizedRequesterException;
import com.example.oulearning.training.domain.repository.TrainingRequestRepository;
import com.example.oulearning.training.domain.repository.TrainingRequestSearchCriteria;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TrainingApplicationServicesTest {

    private InMemoryTrainingRequestRepository trainingRequestRepository;
    private InMemoryOuRepository ouRepository;
    private InMemoryEmployeeRepository employeeRepository;
    private MockTrainingBudgetPort trainingBudgetPort;
    private Clock fixedClock;

    private SubmitTrainingRequestService submitService;
    private ApproveTrainingRequestService approveService;
    private RejectTrainingRequestService rejectService;
    private GetTrainingRequestService getService;
    private GetTrainingRequestsService searchService;

    private final UUID targetOuId = UUID.randomUUID();
    private final UUID otherOuId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        trainingRequestRepository = new InMemoryTrainingRequestRepository();
        ouRepository = new InMemoryOuRepository();
        employeeRepository = new InMemoryEmployeeRepository();
        trainingBudgetPort = new MockTrainingBudgetPort();
        fixedClock = Clock.fixed(Instant.parse("2026-08-17T12:00:00Z"), ZoneId.of("UTC"));

        submitService = new SubmitTrainingRequestService(
                trainingRequestRepository, ouRepository, employeeRepository, trainingBudgetPort, fixedClock);
        approveService = new ApproveTrainingRequestService(
                trainingRequestRepository, employeeRepository, trainingBudgetPort, fixedClock);
        rejectService = new RejectTrainingRequestService(
                trainingRequestRepository, employeeRepository, trainingBudgetPort, fixedClock);
        getService = new GetTrainingRequestService(trainingRequestRepository);
        searchService = new GetTrainingRequestsService(trainingRequestRepository, ouRepository);

        // Setup OU with owner CK0001
        final var targetOu = OrganizationalUnit.leaf(
                com.example.oulearning.organization.domain.unit.OuId.of(targetOuId),
                OuName.of("Engineering"),
                Set.of(com.example.oulearning.organization.domain.employee.CorporateKey.of("CK0001")),
                Set.of());
        ouRepository.save(targetOu);

        // Setup Employees in target OU
        final var emp1 = Employee.of(
                com.example.oulearning.organization.domain.employee.CorporateKey.of("CK0001"),
                FullName.of("Alice", "Smith"),
                Email.of("alice@example.com"),
                Phone.of("+34600111222"),
                EmployeeRole.MANAGER,
                com.example.oulearning.organization.domain.unit.OuId.of(targetOuId));
        final var emp2 = Employee.of(
                com.example.oulearning.organization.domain.employee.CorporateKey.of("CK0002"),
                FullName.of("Bob", "Jones"),
                Email.of("bob@example.com"),
                EmployeeRole.EMPLOYEE,
                com.example.oulearning.organization.domain.unit.OuId.of(targetOuId));
        // Setup Employee in other OU
        final var empOther = Employee.of(
                com.example.oulearning.organization.domain.employee.CorporateKey.of("CK0099"),
                FullName.of("Charlie", "Brown"),
                Email.of("charlie@example.com"),
                EmployeeRole.EMPLOYEE,
                com.example.oulearning.organization.domain.unit.OuId.of(otherOuId));

        employeeRepository.save(emp1);
        employeeRepository.save(emp2);
        employeeRepository.save(empOther);
    }

    @Test
    @DisplayName("should submit training request in DRAFT and reserve budget")
    void should_submitTrainingRequest_successfully() {
        final var command = new SubmitTrainingRequestCommand(
                null,
                targetOuId,
                "CK0001",
                "Advanced Hexagonal Architecture",
                new BigDecimal("2500.00"),
                "EUR",
                "UPSKILLING",
                null,
                24,
                true,
                Set.of("CK0001", "CK0002"));

        final var requestId = submitService.execute(command);
        assertThat(requestId).isNotNull();

        final var retrievedOpt = getService.execute(new GetTrainingRequestQuery(requestId));
        assertThat(retrievedOpt).isPresent();
        final var tr = retrievedOpt.get();
        assertThat(tr.name().value()).isEqualTo("Advanced Hexagonal Architecture");
        assertThat(tr.status()).isEqualTo(TrainingRequestStatus.DRAFT);
        assertThat(tr.fiscalYear().value()).isEqualTo(2026);
        assertThat(tr.assistants()).hasSize(2);

        assertThat(trainingBudgetPort.reservedCalls).hasSize(1);
        assertThat(trainingBudgetPort.reservedCalls.get(0).amount).isEqualByComparingTo("2500.00");
    }

    @Test
    @DisplayName("should approve training request and consume budget when manager approves")
    void should_approveTrainingRequest_successfully() {
        final var command = new SubmitTrainingRequestCommand(
                null,
                targetOuId,
                "CK0001",
                "Advanced Hexagonal Architecture",
                new BigDecimal("2500.00"),
                "EUR",
                "UPSKILLING",
                null,
                24,
                true,
                Set.of("CK0001"));

        final var requestId = submitService.execute(command);

        approveService.execute(new ApproveTrainingRequestCommand(requestId, "CK0001", "Approved for Q3"));

        final var updated = getService.execute(new GetTrainingRequestQuery(requestId)).orElseThrow();
        assertThat(updated.status()).isEqualTo(TrainingRequestStatus.APPROVED);
        assertThat(updated.optionalReviewedBy()).contains(CorporateKey.of("CK0001"));
        assertThat(updated.optionalManagerNotes().get().value()).isEqualTo("Approved for Q3");

        assertThat(trainingBudgetPort.consumedCalls).hasSize(1);
        assertThat(trainingBudgetPort.consumedCalls.get(0).amount).isEqualByComparingTo("2500.00");
    }

    @Test
    @DisplayName("should reject training request and release budget when manager rejects")
    void should_rejectTrainingRequest_successfully() {
        final var command = new SubmitTrainingRequestCommand(
                null,
                targetOuId,
                "CK0001",
                "Advanced Hexagonal Architecture",
                new BigDecimal("2500.00"),
                "EUR",
                "UPSKILLING",
                null,
                24,
                true,
                Set.of("CK0001"));

        final var requestId = submitService.execute(command);

        rejectService.execute(new RejectTrainingRequestCommand(
                requestId, "CK0001", "Insufficient budget allocation", "Try again next year"));

        final var updated = getService.execute(new GetTrainingRequestQuery(requestId)).orElseThrow();
        assertThat(updated.status()).isEqualTo(TrainingRequestStatus.REJECTED);
        assertThat(updated.optionalRejectionReason().get().value()).isEqualTo("Insufficient budget allocation");

        assertThat(trainingBudgetPort.releasedCalls).hasSize(1);
        assertThat(trainingBudgetPort.releasedCalls.get(0).amount).isEqualByComparingTo("2500.00");
    }

    @Test
    @DisplayName("should throw UnauthorizedManagerException when non-manager employee attempts approval")
    void should_throw_whenNonManagerAttemptsApproval() {
        final var command = new SubmitTrainingRequestCommand(
                null,
                targetOuId,
                "CK0001",
                "DDD Workshop",
                new BigDecimal("1000.00"),
                "EUR",
                "UPSKILLING",
                null,
                16,
                true,
                Set.of("CK0001"));

        final var requestId = submitService.execute(command);

        assertThatThrownBy(() -> approveService.execute(
                new ApproveTrainingRequestCommand(requestId, "CK0002", "Approve!")))
                .isInstanceOf(UnauthorizedManagerException.class)
                .hasMessageContaining("Required role: MANAGER");
    }

    @Test
    @DisplayName("should filter training requests by OU name and status")
    void should_filterByOuNameAndStatus() {
        submitService.execute(new SubmitTrainingRequestCommand(
                null, targetOuId, "CK0001", "DDD Workshop",
                new BigDecimal("1000.00"), "EUR", "UPSKILLING", null,
                16, true, Set.of("CK0001")));

        final var list = searchService.execute(GetTrainingRequestsQuery.of(
                List.of(), List.of("Engineering"), "DRAFT", 2026));

        assertThat(list).hasSize(1);
        assertThat(list.get(0).name().value()).isEqualTo("DDD Workshop");
    }

    // In-memory test fakes
    static class MockTrainingBudgetPort implements TrainingBudgetPort {
        record Call(UUID ouId, int fiscalYear, BigDecimal amount, String currency) {}
        final List<Call> reservedCalls = new ArrayList<>();
        final List<Call> consumedCalls = new ArrayList<>();
        final List<Call> releasedCalls = new ArrayList<>();

        @Override
        public void reserveBudget(UUID ouId, int fiscalYear, BigDecimal amount, String currencyCode) {
            reservedCalls.add(new Call(ouId, fiscalYear, amount, currencyCode));
        }

        @Override
        public void consumeBudget(UUID ouId, int fiscalYear, BigDecimal amount, String currencyCode) {
            consumedCalls.add(new Call(ouId, fiscalYear, amount, currencyCode));
        }

        @Override
        public void releaseBudget(UUID ouId, int fiscalYear, BigDecimal amount, String currencyCode) {
            releasedCalls.add(new Call(ouId, fiscalYear, amount, currencyCode));
        }
    }

    static class InMemoryTrainingRequestRepository implements TrainingRequestRepository {
        private final Map<TrainingRequestId, TrainingRequest> store = new HashMap<>();

        @Override
        public void save(TrainingRequest trainingRequest) {
            store.put(trainingRequest.id(), trainingRequest);
        }

        @Override
        public Optional<TrainingRequest> findById(TrainingRequestId id) {
            return Optional.ofNullable(store.get(id));
        }

        @Override
        public List<TrainingRequest> findByOuId(OuId ouId) {
            return store.values().stream().filter(tr -> tr.ouId().equals(ouId)).toList();
        }

        @Override
        public List<TrainingRequest> findByOuIdAndFiscalYear(OuId ouId, FiscalYear fiscalYear) {
            return store.values().stream()
                    .filter(tr -> tr.ouId().equals(ouId) && tr.fiscalYear().equals(fiscalYear))
                    .toList();
        }

        @Override
        public List<TrainingRequest> findByFiscalYear(FiscalYear fiscalYear) {
            return store.values().stream().filter(tr -> tr.fiscalYear().equals(fiscalYear)).toList();
        }

        @Override
        public List<TrainingRequest> findByCriteria(TrainingRequestSearchCriteria criteria) {
            return store.values().stream()
                    .filter(tr -> criteria.ouIds().isEmpty() || criteria.ouIds().contains(tr.ouId()))
                    .filter(tr -> criteria.status() == null || tr.status() == criteria.status())
                    .filter(tr -> criteria.fiscalYear() == null || tr.fiscalYear().equals(criteria.fiscalYear()))
                    .toList();
        }
    }

    static class InMemoryOuRepository implements OrganizationalUnitRepository {
        private final Map<com.example.oulearning.organization.domain.unit.OuId, OrganizationalUnit> store = new HashMap<>();

        @Override
        public void save(OrganizationalUnit unit) {
            store.put(unit.id(), unit);
        }

        @Override
        public Optional<OrganizationalUnit> find(OuSearchCriteria criteria) {
            if (criteria.id() != null) {
                return Optional.ofNullable(store.get(criteria.id()));
            }
            if (criteria.name() != null) {
                return store.values().stream()
                        .filter(u -> u.name().equals(criteria.name()))
                        .findFirst();
            }
            return store.values().stream().findFirst();
        }
    }

    static class InMemoryEmployeeRepository implements EmployeeRepository {
        private final Map<com.example.oulearning.organization.domain.employee.CorporateKey, Employee> store = new HashMap<>();

        @Override
        public void save(Employee employee) {
            store.put(employee.corporateKey(), employee);
        }

        @Override
        public Optional<Employee> findByCorporateKey(com.example.oulearning.organization.domain.employee.CorporateKey corporateKey) {
            return Optional.ofNullable(store.get(corporateKey));
        }

        @Override
        public List<Employee> findByOuId(com.example.oulearning.organization.domain.unit.OuId ouId) {
            return store.values().stream().filter(e -> e.ouId().equals(ouId)).toList();
        }

        @Override
        public List<Employee> findByOuIds(Collection<com.example.oulearning.organization.domain.unit.OuId> ouIds) {
            return store.values().stream().filter(e -> ouIds.contains(e.ouId())).toList();
        }

        @Override
        public void delete(com.example.oulearning.organization.domain.employee.CorporateKey corporateKey) {
            store.remove(corporateKey);
        }
    }
}
