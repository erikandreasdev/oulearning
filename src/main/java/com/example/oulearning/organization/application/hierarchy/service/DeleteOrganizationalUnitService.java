package com.example.oulearning.organization.application.hierarchy.service;

import com.example.oulearning.organization.application.hierarchy.port.in.DeleteOrganizationalUnitUseCase;
import com.example.oulearning.organization.application.hierarchy.exception.OrganizationalUnitNotFoundException;

import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
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
