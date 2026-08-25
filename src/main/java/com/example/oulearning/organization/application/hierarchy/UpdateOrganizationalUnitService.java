package com.example.oulearning.organization.application.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.Name;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitRepository;
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
