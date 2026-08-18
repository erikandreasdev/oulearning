package com.example.oulearning.organization.application.port.in.usecase.snapshot;

import com.example.oulearning.organization.domain.organization.Organization;
import java.util.Optional;

/**
 * Use case input port for retrieving the latest cached organization hierarchy snapshot.
 */
public interface GetLatestOrganizationUseCase {
    Optional<Organization> execute();
}
