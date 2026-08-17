package com.example.oulearning.organization.application;

import com.example.oulearning.organization.domain.employee.CorporateKey;
import com.example.oulearning.organization.domain.unit.OrganizationalUnit;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuName;
import com.example.oulearning.organization.domain.unit.OuType;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating the creation of an Organizational Unit aggregate.
 */
@Service
@Transactional
public class CreateOrganizationalUnitService implements CreateOrganizationalUnitUseCase {

    private final OrganizationalUnitRepository repository;

    public CreateOrganizationalUnitService(OrganizationalUnitRepository repository) {
        this.repository = Objects.requireNonNull(repository, "OrganizationalUnitRepository cannot be null");
    }

    @Override
    public UUID execute(CreateOrganizationalUnitCommand command) {
        Objects.requireNonNull(command, "CreateOrganizationalUnitCommand cannot be null");

        final var ouId = command.id() != null ? OuId.of(command.id()) : OuId.of(UUID.randomUUID());
        final var name = OuName.of(command.name());
        final var ouType = command.ouType() != null ? OuType.valueOf(command.ouType()) : OuType.SUBAREA;

        final var owners = command.ownerCorporateKeys().stream()
                .map(CorporateKey::of)
                .collect(Collectors.toSet());

        final var parents = command.parentIds().stream()
                .map(OuId::of)
                .collect(Collectors.toSet());

        final var children = command.childIds().stream()
                .map(OuId::of)
                .collect(Collectors.toSet());

        final var unit = OrganizationalUnit.of(ouId, name, ouType, owners, parents, children);
        repository.save(unit);

        return unit.id().value();
    }
}
