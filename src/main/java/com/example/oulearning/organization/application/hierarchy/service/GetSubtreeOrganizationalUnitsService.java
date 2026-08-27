package com.example.oulearning.organization.application.hierarchy.service;

import com.example.oulearning.organization.application.hierarchy.exception.OrganizationalUnitNotFoundException;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.GetSubtreeOrganizationalUnitsUseCase;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GetSubtreeOrganizationalUnitsService implements GetSubtreeOrganizationalUnitsUseCase {

    private final OrganizationalUnitRepository organizationalUnitRepository;

    public GetSubtreeOrganizationalUnitsService(final OrganizationalUnitRepository organizationalUnitRepository) {
        this.organizationalUnitRepository = organizationalUnitRepository;
    }

    @Override
    public List<OrganizationalUnit> execute(final OrganizationalUnitId id) {
        final var subtree = organizationalUnitRepository.findSubtreeById(id);
        if (subtree.isEmpty()) {
            throw new OrganizationalUnitNotFoundException(id);
        }
        return subtree;
    }
}
