package com.example.oulearning.organization.application.service.snapshot;

import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.organization.repository.OrganizationRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.oulearning.organization.application.port.in.query.GetOrganizationSnapshotQuery;
import com.example.oulearning.organization.application.port.in.usecase.snapshot.GetOrganizationSnapshotUseCase;

/**
 * Service orchestrating snapshot query by ID or timestamp.
 */
@Service
@Transactional(readOnly = true)
public class GetOrganizationSnapshotService implements GetOrganizationSnapshotUseCase {

    private final OrganizationRepository repository;

    public GetOrganizationSnapshotService(OrganizationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "OrganizationRepository cannot be null");
    }

    @Override
    public Optional<Organization> execute(GetOrganizationSnapshotQuery query) {
        Objects.requireNonNull(query, "GetOrganizationSnapshotQuery cannot be null");

        if (query.snapshotId() != null) {
            return repository.findBySnapshotId(SnapshotId.of(query.snapshotId()));
        } else if (query.atTimestamp() != null) {
            return repository.findAt(query.atTimestamp());
        }

        return Optional.empty();
    }
}
