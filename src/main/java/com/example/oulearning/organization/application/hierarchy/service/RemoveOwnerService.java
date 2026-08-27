package com.example.oulearning.organization.application.hierarchy.service;

import com.example.oulearning.organization.application.hierarchy.port.in.RemoveOwnerCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.RemoveOwnerUseCase;
import com.example.oulearning.organization.application.hierarchy.exception.OrganizationalUnitNotFoundException;

import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoveOwnerService implements RemoveOwnerUseCase {

    private final OrganizationalUnitRepository organizationalUnitRepository;

    public RemoveOwnerService(final OrganizationalUnitRepository organizationalUnitRepository) {
        this.organizationalUnitRepository = organizationalUnitRepository;
    }

    @Override
    public void execute(final RemoveOwnerCommand command) {
        final var organizationalUnit = organizationalUnitRepository.findById(command.ouId())
                .orElseThrow(() -> new OrganizationalUnitNotFoundException(command.ouId()));
        final var updated = organizationalUnit.removeOwners(command.employeeIds());
        organizationalUnitRepository.save(updated);
    }
}
