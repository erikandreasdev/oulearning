package com.example.oulearning.training.application;

import com.example.oulearning.organization.domain.employee.EmployeeRole;
import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.training.application.port.out.TrainingBudgetPort;
import com.example.oulearning.training.domain.CorporateKey;
import com.example.oulearning.training.domain.ManagerNotes;
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
 * Service orchestrating the approval of a training request by an authorized manager.
 * Consumes the reserved training cost from the target OU's budget upon approval.
 */
@Service
@Transactional
public class ApproveTrainingRequestService implements ApproveTrainingRequestUseCase {

    private final TrainingRequestRepository trainingRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final TrainingBudgetPort trainingBudgetPort;
    private final Clock clock;

    public ApproveTrainingRequestService(
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
    public void execute(ApproveTrainingRequestCommand command) {
        Objects.requireNonNull(command, "ApproveTrainingRequestCommand cannot be null");

        // 1. Validate Manager Authorization
        final var managerOrgCk = com.example.oulearning.organization.domain.employee.CorporateKey.of(command.managerCorporateKey());
        final var managerEmployee = employeeRepository.findByCorporateKey(managerOrgCk)
                .orElseThrow(() -> new UnauthorizedManagerException(
                        "Employee '%s' not found".formatted(command.managerCorporateKey())));

        if (managerEmployee.role() != EmployeeRole.MANAGER && managerEmployee.role() != EmployeeRole.ADMIN) {
            throw new UnauthorizedManagerException(
                    "Employee '%s' is not authorized to approve training requests. Required role: MANAGER"
                            .formatted(command.managerCorporateKey()));
        }

        // 2. Load Training Request
        final var requestId = TrainingRequestId.of(command.requestId());
        final var trainingRequest = trainingRequestRepository.findById(requestId)
                .orElseThrow(() -> new NoSuchElementException("Training request '%s' not found".formatted(command.requestId())));

        // 3. Execute Domain Transition to APPROVED
        final var managerKey = CorporateKey.of(command.managerCorporateKey());
        final var notes = command.managerNotes() != null ? ManagerNotes.of(command.managerNotes()) : null;
        final var approvedRequest = trainingRequest.approve(managerKey, notes, Instant.now(clock));

        // 4. Persist updated Training Request
        trainingRequestRepository.save(approvedRequest);

        // 5. Consume reserved budget in target OU
        trainingBudgetPort.consumeBudget(
                approvedRequest.ouId().value(),
                approvedRequest.fiscalYear().value(),
                approvedRequest.cost().amount(),
                approvedRequest.cost().currency());
    }
}
