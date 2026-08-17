package com.example.oulearning.training.application;

import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import com.example.oulearning.shared.domain.fiscal.FiscalYear;
import com.example.oulearning.training.domain.OuId;
import com.example.oulearning.training.domain.TrainingRequest;
import com.example.oulearning.training.domain.TrainingRequestStatus;
import com.example.oulearning.training.domain.repository.TrainingRequestRepository;
import com.example.oulearning.training.domain.repository.TrainingRequestSearchCriteria;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating training request searches with support for OU IDs, OU Names, Status, and Fiscal Year filters.
 */
@Service
@Transactional(readOnly = true)
public class GetTrainingRequestsService implements GetTrainingRequestsUseCase {

    private final TrainingRequestRepository repository;
    private final OrganizationalUnitRepository ouRepository;

    public GetTrainingRequestsService(
            TrainingRequestRepository repository,
            OrganizationalUnitRepository ouRepository) {
        this.repository = Objects.requireNonNull(repository, "TrainingRequestRepository cannot be null");
        this.ouRepository = Objects.requireNonNull(ouRepository, "OrganizationalUnitRepository cannot be null");
    }

    @Override
    public List<TrainingRequest> execute(GetTrainingRequestsQuery query) {
        Objects.requireNonNull(query, "GetTrainingRequestsQuery cannot be null");

        final var targetOuIds = new HashSet<OuId>();

        // 1. Add direct OU IDs
        for (final var rawOuId : query.ouIds()) {
            targetOuIds.add(OuId.of(rawOuId));
        }

        // 2. Resolve OU Names to OU IDs
        for (final var ouNameStr : query.ouNames()) {
            if (ouNameStr != null && !ouNameStr.isBlank()) {
                final var ouOpt = ouRepository.find(OuSearchCriteria.byName(OuName.of(ouNameStr.strip()), false));
                ouOpt.ifPresent(ou -> targetOuIds.add(OuId.of(ou.id().value())));
            }
        }

        // 3. Status filter
        final var status = (query.status() != null && !query.status().isBlank())
                ? TrainingRequestStatus.valueOf(query.status().strip().toUpperCase())
                : null;

        // 4. Fiscal Year filter
        final var fiscalYear = query.fiscalYear() != null ? FiscalYear.of(query.fiscalYear()) : null;

        final var criteria = new TrainingRequestSearchCriteria(targetOuIds, status, fiscalYear);
        return repository.findByCriteria(criteria);
    }
}
