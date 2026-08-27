package com.example.oulearning.organization.application.hierarchy.service;

import com.example.oulearning.organization.application.hierarchy.exception.OrganizationalUnitNotFoundException;
import com.example.oulearning.organization.application.hierarchy.port.in.command.UpdateOrganizationalUnitCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.UpdateOrganizationalUnitUseCase;
import com.example.oulearning.organization.domain.hierarchy.model.Name;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateOrganizationalUnitService implements UpdateOrganizationalUnitUseCase {

    private final OrganizationalUnitRepository organizationalUnitRepository;

    public UpdateOrganizationalUnitService(final OrganizationalUnitRepository organizationalUnitRepository) {
        this.organizationalUnitRepository = organizationalUnitRepository;
    }

    @Override
    public void execute(final UpdateOrganizationalUnitCommand command) {
        final var organizationalUnit = organizationalUnitRepository.findById(command.id())
                .orElseThrow(() -> new OrganizationalUnitNotFoundException(command.id()));
        final var updated = organizationalUnit.rename(Name.of(command.name()));
        organizationalUnitRepository.save(updated);
    }
}
