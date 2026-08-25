package com.example.oulearning.organization.application.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitRepository;
import org.springframework.stereotype.Service;

@Service
public class GetOrganizationalUnitService implements GetOrganizationalUnitUseCase {

    private final OrganizationalUnitRepository organizationalUnitRepository;

    public GetOrganizationalUnitService(final OrganizationalUnitRepository organizationalUnitRepository) {
        this.organizationalUnitRepository = organizationalUnitRepository;
    }

    @Override
    public OrganizationalUnit execute(final OrganizationalUnitId id) {
        return organizationalUnitRepository.findById(id)
                .orElseThrow(() -> new OrganizationalUnitNotFoundException(id));
    }
}
