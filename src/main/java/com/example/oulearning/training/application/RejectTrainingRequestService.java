package com.example.oulearning.training.application;

import com.example.oulearning.organization.domain.employee.EmployeeRole;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.training.application.port.out.TrainingBudgetPort;
import com.example.oulearning.training.domain.CorporateKey;
import com.example.oulearning.training.domain.ManagerNotes;
import com.example.oulearning.training.domain.RejectionReason;
import com.example.oulearning.training.domain.TrainingRequestId;
import com.example.oulearning.training.domain.exception.UnauthorizedManagerException;
import com.example.oulearning.training.domain.repository.TrainingRequestRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating the rejection of a training request by an authorized manager.
 * Releases the reserved training cost back to the target OU's available budget.
 */
@Service
@Transactional
public class RejectTrainingRequestService implements RejectTrainingRequestUseCase {

    private final TrainingRequestRepository trainingRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final TrainingBudgetPort trainingBudgetPort;
    private final Clock clock;

    public RejectTrainingRequestService(
            TrainingRequestRepository trainingRequestRepository,
            EmployeeRepository employeeRepository,
            TrainingBudgetPort trainingBudgetPort,
            Clock clock) {
        this.trainingRequestRepository = Objects.requireNonNull(trainingRequestRepository, "TrainingRequestRepository cannot be null");
        this.employeeRepository = Objects.requireNonNull(employeeRepository, "EmployeeRepository cannot be null");
        this.trainingBudgetPort = Objects.requireNonNull(trainingBudgetPort, "TrainingBudgetPort cannot be null");
        this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
    }

    @Override
    public void execute(RejectTrainingRequestCommand command) {
        Objects.requireNonNull(command, "RejectTrainingRequestCommand cannot be null");

        // 1. Validate Manager Authorization
        final var managerOrgCk = com.example.oulearning.organization.domain.employee.CorporateKey.of(command.managerCorporateKey());
        final var managerEmployee = employeeRepository.findByCorporateKey(managerOrgCk)
                .orElseThrow(() -> new UnauthorizedManagerException(
                        "Employee '%s' not found".formatted(command.managerCorporateKey())));

        if (managerEmployee.role() != EmployeeRole.MANAGER && managerEmployee.role() != EmployeeRole.ADMIN) {
            throw new UnauthorizedManagerException(
                    "Employee '%s' is not authorized to reject training requests. Required role: MANAGER"
                            .formatted(command.managerCorporateKey()));
        }

        // 2. Load Training Request
        final var requestId = TrainingRequestId.of(command.requestId());
        final var trainingRequest = trainingRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Training request '%s' not found".formatted(command.requestId())));

        // 3. Execute Domain Transition to REJECTED with mandatory reason
        final var managerKey = CorporateKey.of(command.managerCorporateKey());
        final var reason = RejectionReason.of(command.rejectionReason());
        final var notes = command.managerNotes() != null ? ManagerNotes.of(command.managerNotes()) : null;
        final var rejectedRequest = trainingRequest.reject(managerKey, reason, notes, Instant.now(clock));

        // 4. Persist updated Training Request
        trainingRequestRepository.save(rejectedRequest);

        // 5. Release reserved budget back to target OU available funds
        trainingBudgetPort.releaseBudget(
                rejectedRequest.ouId().value(),
                rejectedRequest.fiscalYear().value(),
                rejectedRequest.cost().amount(),
                rejectedRequest.cost().currency());
    }
}
