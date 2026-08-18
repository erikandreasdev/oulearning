package com.example.oulearning.organization.application.port.in.usecase.snapshot;

import java.util.UUID;
import com.example.oulearning.organization.application.port.in.command.CreateOrganizationSnapshotCommand;

/**
 * Use case input port for creating and persisting an organization hierarchy snapshot.
 */
public interface CreateOrganizationSnapshotUseCase {
    UUID execute(CreateOrganizationSnapshotCommand command);
}
