package com.example.oulearning.organization.application.port.in.usecase.snapshot;

import java.util.UUID;
import com.example.oulearning.organization.application.port.in.command.UploadOrganizationSnapshotCommand;

/**
 * Use case interface for uploading and activating a new organization hierarchy snapshot.
 */
public interface UploadOrganizationSnapshotUseCase {

    UUID execute(UploadOrganizationSnapshotCommand command);
}
