package com.example.oulearning.organization.application;

import com.example.oulearning.organization.domain.organization.Organization;
import java.util.Optional;

/**
 * Use case input port for retrieving an organization snapshot by ID or point-in-time timestamp.
 */
public interface GetOrganizationSnapshotUseCase {
    Optional<Organization> execute(GetOrganizationSnapshotQuery query);
}
