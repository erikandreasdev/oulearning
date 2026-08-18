package com.example.oulearning.organization.application.service.snapshot;

import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.repository.OrganizationRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.oulearning.organization.application.port.in.usecase.snapshot.GetLatestOrganizationUseCase;

/**
 * Service orchestrating the retrieval of the latest organization snapshot.
 */
@Service
@Transactional(readOnly = true)
public class GetLatestOrganizationService implements GetLatestOrganizationUseCase {

    private final OrganizationRepository repository;

    public GetLatestOrganizationService(OrganizationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "OrganizationRepository cannot be null");
    }

    @Override
    public Optional<Organization> execute() {
        return repository.findLatest();
    }
}
