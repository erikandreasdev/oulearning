package com.example.oulearning.organization.application.hierarchy.service;

import com.example.oulearning.organization.application.hierarchy.port.in.AssignOwnerCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.AssignOwnerUseCase;
import com.example.oulearning.organization.application.hierarchy.exception.OrganizationalUnitNotFoundException;

import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import org.springframework.stereotype.Service;

@Service
public class AssignOwnerService implements AssignOwnerUseCase {

    private final OrganizationalUnitRepository organizationalUnitRepository;

    public AssignOwnerService(final OrganizationalUnitRepository organizationalUnitRepository) {
        this.organizationalUnitRepository = organizationalUnitRepository;
    }

    @Override
    public void execute(final AssignOwnerCommand command) {
        final var organizationalUnit = organizationalUnitRepository.findById(command.ouId())
                .orElseThrow(() -> new OrganizationalUnitNotFoundException(command.ouId()));
        final var updated = organizationalUnit.addOwners(command.employeeIds());
        organizationalUnitRepository.save(updated);
    }
}
