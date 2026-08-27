package com.example.oulearning.organization.application.hierarchy.service;

import com.example.oulearning.organization.application.hierarchy.exception.OrganizationalUnitNotFoundException;
import com.example.oulearning.organization.application.hierarchy.port.in.command.RemoveMemberCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.RemoveMemberUseCase;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import org.springframework.stereotype.Service;

@Service
public class RemoveMemberService implements RemoveMemberUseCase {

    private final OrganizationalUnitRepository organizationalUnitRepository;

    public RemoveMemberService(final OrganizationalUnitRepository organizationalUnitRepository) {
        this.organizationalUnitRepository = organizationalUnitRepository;
    }

    @Override
    public void execute(final RemoveMemberCommand command) {
        final var organizationalUnit = organizationalUnitRepository.findById(command.ouId())
                .orElseThrow(() -> new OrganizationalUnitNotFoundException(command.ouId()));
        final var updated = organizationalUnit.removeMembers(command.employeeIds());
        organizationalUnitRepository.save(updated);
    }
}
