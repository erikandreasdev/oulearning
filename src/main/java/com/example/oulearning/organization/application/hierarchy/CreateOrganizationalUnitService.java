package com.example.oulearning.organization.application.hierarchy;

import com.example.oulearning.organization.domain.hierarchy.IdGenerator;
import com.example.oulearning.organization.domain.hierarchy.Name;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnit;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitId;
import com.example.oulearning.organization.domain.hierarchy.OrganizationalUnitRepository;
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
