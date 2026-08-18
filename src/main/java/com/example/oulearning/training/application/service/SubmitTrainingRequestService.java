package com.example.oulearning.training.application.service;

import com.example.oulearning.organization.domain.employee.repository.EmployeeRepository;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.training.application.port.out.TrainingBudgetPort;
import com.example.oulearning.training.domain.request.vo.identity.CorporateKey;
import com.example.oulearning.training.domain.request.vo.identity.OuId;
import com.example.oulearning.training.domain.request.vo.details.TrainingCost;
import com.example.oulearning.training.domain.request.vo.details.TrainingHours;
import com.example.oulearning.training.domain.request.vo.details.TrainingName;
import com.example.oulearning.training.domain.request.vo.details.TrainingPurpose;
import com.example.oulearning.training.domain.request.vo.details.TrainingPurposeType;
import com.example.oulearning.training.domain.request.TrainingRequest;
import com.example.oulearning.training.domain.request.vo.identity.TrainingRequestId;
import com.example.oulearning.training.domain.request.exception.InvalidAssistantException;
import com.example.oulearning.training.domain.request.exception.UnauthorizedRequesterException;
import com.example.oulearning.training.domain.request.repository.TrainingRequestRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.oulearning.training.application.port.in.command.SubmitTrainingRequestCommand;
import com.example.oulearning.training.application.port.in.usecase.SubmitTrainingRequestUseCase;

/**
 * Service orchestrating the validation and submission of a training request.
 * Enforces that:
 * 1. The requester is a registered owner of the target OU.
 * 2. All assistants are active employee members of the target OU.
 * 3. The request is assigned the current Fiscal Year from Clock and starts in DRAFT status.
 * 4. The requested cost is reserved in the target OU budget for the fiscal year.
 */
@Service
@Transactional
public class SubmitTrainingRequestService implements SubmitTrainingRequestUseCase {

    private final TrainingRequestRepository trainingRequestRepository;
    private final OrganizationalUnitRepository ouRepository;
    private final EmployeeRepository employeeRepository;
    private final TrainingBudgetPort trainingBudgetPort;
    private final Clock clock;

    public SubmitTrainingRequestService(
            TrainingRequestRepository trainingRequestRepository,
            OrganizationalUnitRepository ouRepository,
            EmployeeRepository employeeRepository,
            TrainingBudgetPort trainingBudgetPort,
            Clock clock) {
        this.trainingRequestRepository = Objects.requireNonNull(trainingRequestRepository, "TrainingRequestRepository cannot be null");
        this.ouRepository = Objects.requireNonNull(ouRepository, "OrganizationalUnitRepository cannot be null");
        this.employeeRepository = Objects.requireNonNull(employeeRepository, "EmployeeRepository cannot be null");
        this.trainingBudgetPort = Objects.requireNonNull(trainingBudgetPort, "TrainingBudgetPort cannot be null");
        this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
    }

    @Override
    public UUID execute(SubmitTrainingRequestCommand command) {
        Objects.requireNonNull(command, "SubmitTrainingRequestCommand cannot be null");

        // 1. Verify Target OU exists
        final var targetOuId = com.example.oulearning.organization.domain.unit.OuId.of(command.ouId());
        final var targetOu = ouRepository.find(OuSearchCriteria.byId(targetOuId, false))
                .orElseThrow(() -> new NoSuchElementException("Organizational Unit '%s' not found".formatted(command.ouId())));

        // 2. Validate that Requester is an owner of the target OU
        final var requesterOrgCk = com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey.of(command.requesterCorporateKey());
        if (!targetOu.owners().contains(requesterOrgCk)) {
            throw new UnauthorizedRequesterException(
                    "Employee '%s' is not an authorized owner of OU '%s'".formatted(
                            command.requesterCorporateKey(), targetOu.name().value()));
        }

        // 3. Validate that each Assistant is an employee assigned to the target OU
        if (command.assistantCorporateKeys().isEmpty()) {
            throw new InvalidAssistantException("At least one assistant is required for training request");
        }

        final var assistantsSet = new HashSet<CorporateKey>();
        for (final var assistantKeyStr : command.assistantCorporateKeys()) {
            final var assistantOrgCk = com.example.oulearning.organization.domain.employee.vo.identity.CorporateKey.of(assistantKeyStr);
            final var employeeOpt = employeeRepository.findByCorporateKey(assistantOrgCk);
            if (employeeOpt.isEmpty()) {
                throw new InvalidAssistantException("Assistant employee '%s' not found".formatted(assistantKeyStr));
            }
            final var employee = employeeOpt.get();
            if (!employee.ouId().value().equals(command.ouId())) {
                throw new InvalidAssistantException(
                        "Assistant '%s' is not a member of OU '%s'".formatted(assistantKeyStr, targetOu.name().value()));
            }
            assistantsSet.add(CorporateKey.of(assistantKeyStr));
        }

        // 4. Construct TrainingRequest aggregate in DRAFT state
        final var id = command.id() != null
                ? TrainingRequestId.of(command.id())
                : TrainingRequestId.random();
        final var ouId = OuId.of(command.ouId());
        final var requester = CorporateKey.of(command.requesterCorporateKey());
        final var name = TrainingName.of(command.name());
        final var cost = TrainingCost.of(
                command.costAmount(),
                command.costCurrency() != null ? command.costCurrency() : TrainingCost.DEFAULT_CURRENCY);
        final var purposeType = TrainingPurposeType.valueOf(command.purposeType());
        final var purpose = purposeType == TrainingPurposeType.OTHER
                ? TrainingPurpose.other(command.purposeCustomText())
                : TrainingPurpose.of(purposeType);
        final var hours = TrainingHours.of(command.trainingHours());
        final var fiscalYear = FiscalYear.current(clock);
        final var now = Instant.now(clock);

        final var trainingRequest = TrainingRequest.create(
                id,
                ouId,
                requester,
                name,
                cost,
                purpose,
                hours,
                command.availableAtOrgUniversity(),
                Set.copyOf(assistantsSet),
                fiscalYear,
                now);

        // 5. Persist Training Request
        trainingRequestRepository.save(trainingRequest);

        // 6. Reserve cost in target OU budget for fiscal year
        trainingBudgetPort.reserveBudget(command.ouId(), fiscalYear.value(), cost.amount(), cost.currency());

        return trainingRequest.id().value();
    }
}
