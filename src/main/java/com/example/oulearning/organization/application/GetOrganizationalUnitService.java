package com.example.oulearning.organization.application;

import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating organizational unit retrieval queries.
 */
@Service
@Transactional(readOnly = true)
public class GetOrganizationalUnitService implements GetOrganizationalUnitUseCase {

    private final OrganizationalUnitRepository repository;

    public GetOrganizationalUnitService(OrganizationalUnitRepository repository) {
        this.repository = Objects.requireNonNull(repository, "OrganizationalUnitRepository cannot be null");
    }

    @Override
    public Optional<OrganizationalUnit> execute(GetOrganizationalUnitQuery query) {
        Objects.requireNonNull(query, "GetOrganizationalUnitQuery cannot be null");

        if (query.id() != null) {
            final var criteria = OuSearchCriteria.byId(OuId.of(query.id()), query.includeSubtree());
            return repository.find(criteria);
        } else if (query.name() != null) {
            final var criteria = OuSearchCriteria.byName(OuName.of(query.name()));
            return repository.find(criteria);
        }

        return Optional.empty();
    }
}
