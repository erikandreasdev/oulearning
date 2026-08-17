package com.example.oulearning.organization.application;

import com.example.oulearning.organization.domain.organization.Organization;
import com.example.oulearning.organization.domain.organization.SnapshotId;
import com.example.oulearning.organization.domain.organization.repository.OrganizationRepository;
import com.example.oulearning.organization.domain.unit.OuId;
import com.example.oulearning.organization.domain.unit.OuSearchCriteria;
import com.example.oulearning.organization.domain.unit.repository.OrganizationalUnitRepository;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service orchestrating the creation and persistence of an Organization hierarchy snapshot.
 */
@Service
@Transactional
public class CreateOrganizationSnapshotService implements CreateOrganizationSnapshotUseCase {

    private final OrganizationRepository organizationRepository;
    private final OrganizationalUnitRepository unitRepository;

    public CreateOrganizationSnapshotService(
            OrganizationRepository organizationRepository,
            OrganizationalUnitRepository unitRepository) {
        this.organizationRepository = Objects.requireNonNull(organizationRepository, "OrganizationRepository cannot be null");
        this.unitRepository = Objects.requireNonNull(unitRepository, "OrganizationalUnitRepository cannot be null");
    }

    @Override
    public UUID execute(CreateOrganizationSnapshotCommand command) {
        Objects.requireNonNull(command, "CreateOrganizationSnapshotCommand cannot be null");

        final var rootOuId = OuId.of(command.rootOuId());
        final var rootOu = unitRepository
                .find(OuSearchCriteria.byId(rootOuId, true))
                .orElseThrow(() -> new NoSuchElementException(
                        "Root Organizational Unit '%s' not found for snapshot".formatted(command.rootOuId())));

        final var snapshotId = command.snapshotId() != null
                ? SnapshotId.of(command.snapshotId())
                : SnapshotId.of(UUID.randomUUID());
        final var createdAt = command.createdAt() != null ? command.createdAt() : Instant.now();

        final var organization = new Organization(snapshotId, rootOu, createdAt);
        organizationRepository.save(organization);

        return organization.snapshotId().value();
    }
}
