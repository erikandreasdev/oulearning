package com.example.oulearning.organization.application.hierarchy.service;

import com.example.oulearning.organization.application.hierarchy.port.in.command.CreateOrganizationalUnitCommand;
import com.example.oulearning.organization.application.hierarchy.port.in.usecase.CreateOrganizationalUnitUseCase;
import com.example.oulearning.organization.domain.hierarchy.model.IdGenerator;
import com.example.oulearning.organization.domain.hierarchy.model.Name;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.model.OrganizationalUnitId;
import com.example.oulearning.organization.domain.hierarchy.repository.OrganizationalUnitRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateOrganizationalUnitService implements CreateOrganizationalUnitUseCase {

    private final OrganizationalUnitRepository organizationalUnitRepository;
    private final IdGenerator idGenerator;

    public CreateOrganizationalUnitService(
            final OrganizationalUnitRepository organizationalUnitRepository,
            final IdGenerator idGenerator) {
        this.organizationalUnitRepository = organizationalUnitRepository;
        this.idGenerator = idGenerator;
    }

    @Override
    public OrganizationalUnitId execute(final CreateOrganizationalUnitCommand command) {
        final var id = OrganizationalUnitId.of(idGenerator.generate());
        final var name = Name.of(command.name());
        final var organizationalUnit = OrganizationalUnit.create(id, name, command.parentId());
        organizationalUnitRepository.save(organizationalUnit);
        return id;
    }
}
