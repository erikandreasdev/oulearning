package com.example.oulearning.organization.application;

import java.util.UUID;

/**
 * Use case input port for creating and persisting an organization hierarchy snapshot.
 */
public interface CreateOrganizationSnapshotUseCase {
    UUID execute(CreateOrganizationSnapshotCommand command);
}
