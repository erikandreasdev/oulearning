package com.example.oulearning.organization.application.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteOrganizationalUnitService implements DeleteOrganizationalUnitUseCase {

    private final OrganizationalUnitRepository organizationalUnitRepository;

    public DeleteOrganizationalUnitService(final OrganizationalUnitRepository organizationalUnitRepository) {
        this.organizationalUnitRepository = organizationalUnitRepository;
    }

    @Override
    public void execute(final OrganizationalUnitId id) {
        final var organizationalUnit = organizationalUnitRepository.findById(id)
                .orElseThrow(() -> new OrganizationalUnitNotFoundException(id));
        organizationalUnitRepository.save(organizationalUnit.deactivate());
    }
}
