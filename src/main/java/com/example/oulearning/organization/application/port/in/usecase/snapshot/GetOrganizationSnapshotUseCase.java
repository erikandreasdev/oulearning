package com.example.oulearning.organization.application.port.in.usecase.snapshot;

import com.example.oulearning.organization.domain.organization.Organization;
import java.util.Optional;
import com.example.oulearning.organization.application.port.in.query.GetOrganizationSnapshotQuery;

/**
 * Use case input port for retrieving an organization snapshot by ID or point-in-time timestamp.
 */
public interface GetOrganizationSnapshotUseCase {
    Optional<Organization> execute(GetOrganizationSnapshotQuery query);
}
