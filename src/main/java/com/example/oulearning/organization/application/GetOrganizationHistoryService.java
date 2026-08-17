package com.example.oulearning.organization.application;

import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.repository.OrganizationRepository;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating retrieval of organization snapshot history.
 */
@Service
@Transactional(readOnly = true)
public class GetOrganizationHistoryService implements GetOrganizationHistoryUseCase {

    private final OrganizationRepository repository;

    public GetOrganizationHistoryService(OrganizationRepository repository) {
        this.repository = Objects.requireNonNull(repository, "OrganizationRepository cannot be null");
    }

    @Override
    public List<Organization> execute() {
        return repository.findAllHistory();
    }
}
